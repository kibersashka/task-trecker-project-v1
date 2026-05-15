package com.task.tracker.web.controller;

import com.task.tracker.web.client.TaskClient;
import com.task.tracker.web.dto.TagForm;
import com.task.tracker.web.service.UserSession;
import com.task.tracker.web.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller @RequestMapping("/tags") @RequiredArgsConstructor
public class TagController {

    private final TaskClient taskClient;
    private final SessionUtils sessionUtils;

    @GetMapping
    public String list(Model model, HttpSession session) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return "redirect:/login";
        }
        model.addAttribute("tags",taskClient.tags(userSession.bearer(), userSession.getAccountId()));
        model.addAttribute("tagForm",new TagForm());
        model.addAttribute("session",userSession);
        return "tags/list";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute TagForm form, BindingResult bindingResult,
                         HttpSession session, RedirectAttributes flash) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) return "redirect:/login";
        if (bindingResult.hasErrors()) {
            flash.addFlashAttribute("error","Проверьте данные");
            return "redirect:/tags";
        }
        try {
            taskClient.createTag(userSession.bearer(), userSession.getAccountId(), form.getName(), form.getDescription(), form.getColor());
            flash.addFlashAttribute("success", "Тег создан");
        } catch (Exception e) { flash.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/tags";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute TagForm form, HttpSession session, RedirectAttributes flash) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return "redirect:/login";
        }
        try {
            taskClient.updateTag(userSession.bearer(), id, form.getName(), form.getDescription(), form.getColor());
            flash.addFlashAttribute("success", "Тег обновлён");
        } catch (Exception e) { flash.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/tags";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, HttpSession session, RedirectAttributes flash) {
        UserSession us = sessionUtils.get(session);
        if (us == null) {
            return "redirect:/login";
        }
        try {
            taskClient.deleteTag(us.bearer(), id);
            flash.addFlashAttribute("success", "Тег удалён");
        } catch (Exception e) { flash.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/tags";
    }
}
