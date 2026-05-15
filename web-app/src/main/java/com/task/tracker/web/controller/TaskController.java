package com.task.tracker.web.controller;

import com.task.tracker.web.client.AiClient;
import com.task.tracker.web.client.TaskClient;
import com.task.tracker.web.dto.TaskForm;
import com.task.tracker.web.service.UserSession;
import com.task.tracker.web.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TaskController {

    private final TaskClient taskClient;
    private final AiClient aiClient;
    private final SessionUtils sessionUtils;

    @GetMapping("/tasks")
    public String list(@RequestParam(required = false) String status,
                       @RequestParam(required = false) String priority,
                       @RequestParam(required = false) String tagName,
                       Model model,
                       HttpSession session) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return "redirect:/login";
        }

        List<Map<String, Object>> tasks = taskClient.search(userSession.bearer(), userSession.getAccountId(), status, priority, tagName);
        List<Map<String, Object>> tags  = taskClient.tags(userSession.bearer(), userSession.getAccountId());

        model.addAttribute("tasks", tasks);
        model.addAttribute("tags", tags);
        model.addAttribute("taskForm", new TaskForm());
        model.addAttribute("filterStatus", status);
        model.addAttribute("filterPriority", priority);
        model.addAttribute("session", userSession);
        model.addAttribute("statuses", List.of("CREATED","PROCESSING","COMPLETED"));
        model.addAttribute("priorities", List.of("LOW","MIDDLE","HIGH"));
        model.addAttribute("filterTagName", tagName);
        return "tasks/list";
    }

    @PostMapping("/tasks/create")
    public String create(@Valid @ModelAttribute TaskForm form, BindingResult bindingResult,
                         HttpSession session, RedirectAttributes flash) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            flash.addFlashAttribute("error","Проверьте форму");
            return "redirect:/tasks";
        }
        form.setAccountId(userSession.getAccountId());
        try {
            taskClient.create(userSession.bearer(), form);
            flash.addFlashAttribute("success", "Задача создана");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tasks";
    }


    @PostMapping("/tasks/{id}/update")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute TaskForm form,
                         HttpSession session, RedirectAttributes flash) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return "redirect:/login";
        }
        form.setId(id);
        form.setAccountId(userSession.getAccountId());
        try {
            taskClient.update(userSession.bearer(), form);
            flash.addFlashAttribute("success", "Задача обновлена");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tasks";
    }


    @PostMapping("/tasks/{id}/delete")
    public String delete(@PathVariable UUID id, HttpSession session, RedirectAttributes flash) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return "redirect:/login";
        }
        try {
            taskClient.delete(userSession.bearer(), id);
            flash.addFlashAttribute("success", "Задача удалена");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tasks";
    }


    @PostMapping("/api/tasks/{id}/complete")
    @ResponseBody
    public ResponseEntity<?> complete(@PathVariable UUID id, HttpSession session) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return ResponseEntity.status(401).body(Map.of("error","Не авторизован"));
        }
        try {
            return ResponseEntity.ok(taskClient.complete(userSession.bearer(), id));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/tasks/{id}/start")
    @ResponseBody
    public ResponseEntity<?> start(@PathVariable UUID id, HttpSession session) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return ResponseEntity.status(401).body(Map.of("error","Не авторизован"));
        }
        try {
            return ResponseEntity.ok(taskClient.start(userSession.bearer(), id));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/api/reminders")
    @ResponseBody
    public ResponseEntity<?> reminder(@RequestBody Map<String, String> body, HttpSession session) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return ResponseEntity.status(401).body(Map.of("error","Не авторизован"));
        }
        try {
            return ResponseEntity.ok(taskClient.createReminder(userSession.bearer(),
                    UUID.fromString(body.get("taskId")), body.get("reminderDate")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/api/ai/prioritize")
    @ResponseBody
    public ResponseEntity<?> prioritize(HttpSession session) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return ResponseEntity.status(401).body(Map.of("error","Не авторизован"));
        }

        List<Map<String, Object>> allTasks = taskClient.search(userSession.bearer(), userSession.getAccountId(), null, null, null);

        List<Map<String, Object>> activeTasks = allTasks.stream()
                .filter(t -> !"COMPLETED".equals(t.get("status")))
                .toList();

        if (activeTasks.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "generalAdvice", "Нет активных задач для анализа. Создай новые задачи!",
                    "recommendations", List.of()
            ));
        }

        try {
            return ResponseEntity.ok(aiClient.prioritize(userSession.getAccountId(), activeTasks));
        } catch (Exception e) {
            log.error("AI: {}", e.getMessage());
            return ResponseEntity.status(503).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/week")
    public String week(@RequestParam(required = false) String weekStart,
                       Model model, HttpSession session) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return "redirect:/login";
        }

        log.info("weekStart={}", weekStart);
        LocalDate start = weekStart != null
                ? LocalDate.parse(weekStart)
                : LocalDate.now(ZoneOffset.UTC).with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

        log.info("start={}", fmt.format(start));
        List<Map<String, Object>> all = taskClient.search(userSession.bearer(), userSession.getAccountId(), null, null, null);
        Map<String, List<Map<String, Object>>> byDay = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            byDay.put(start.plusDays(i).format(fmt), new ArrayList<>());
        }
        for (Map<String, Object> t : all) {
            Object dd = t.get("dueDate");
            log.info("dd={}", dd);
            if (dd != null) {
                String day = dd.toString().substring(0, 10);
                byDay.computeIfPresent(day, (k, v) -> { v.add(t); return v; });
            }
        }

        log.info("byDay={}", byDay);
        model.addAttribute("byDay", byDay);
        model.addAttribute("weekStart", start.format(fmt));
        model.addAttribute("weekEnd", end.format(fmt));
        model.addAttribute("prevWeek", start.minusDays(7).format(fmt));
        model.addAttribute("nextWeek", start.plusDays(7).format(fmt));
        model.addAttribute("today", LocalDate.now(ZoneOffset.UTC).format(fmt));
        model.addAttribute("session", userSession);
        return "tasks/week";
    }
}
