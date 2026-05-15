package com.task.tracker.web.controller;

import com.task.tracker.web.client.NotificationClient;
import com.task.tracker.web.client.UserClient;
import com.task.tracker.web.service.UserSession;
import com.task.tracker.web.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final UserClient userClient;
    private final NotificationClient notificationClient;
    private final SessionUtils sessionUtils;

    @GetMapping("/home")
    public String landing(HttpSession session) {
        if (sessionUtils.ok(session)) {
            return "redirect:/tasks";
        }
        return "home/index";
    }

    @GetMapping("/top")
    public String top(Model m, HttpSession session) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return "redirect:/login";
        }

        if (!userSession.isAdmin()) {
            return "error/403";
        }

        List<Map<String, Object>> topUsers = userClient.getTop(userSession.bearer());
        m.addAttribute("topUsers", topUsers);
        m.addAttribute("session",  userSession);
        return "home/top";
    }


    @GetMapping("/api/notifications")
    @ResponseBody
    public ResponseEntity<?> getNotifications(HttpSession session) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Не авторизован"));
        }
        List<Map<String, Object>> list = notificationClient.findAll(userSession.bearer(), userSession.getAccountId());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/api/notifications/{id}/read")
    @ResponseBody
    public ResponseEntity<?> markRead(@PathVariable UUID id, HttpSession session) {
        UserSession userSession = sessionUtils.get(session);
        if (userSession == null){
            return ResponseEntity.status(401).body(Map.of("error","Не авторизован"));
        }
        try {
            notificationClient.markAsRead(userSession.bearer(), userSession.getAccountId(), id);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
