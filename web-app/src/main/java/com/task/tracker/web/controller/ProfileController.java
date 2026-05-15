package com.task.tracker.web.controller;


import com.task.tracker.web.client.UserClient;
import com.task.tracker.web.service.UserSession;
import com.task.tracker.web.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller @RequiredArgsConstructor
public class ProfileController {

    private final UserClient userClient;
    private final SessionUtils sessionUtils;

    @GetMapping("/profile")
    public String profile(Model m, HttpSession session) {
        UserSession us = sessionUtils.get(session);
        if (us == null) {
            return "redirect:/login";
        }
        Map<String, Object> info = userClient.info(us.bearer(), us.getAccountId());
        int xp = info.get("xp") instanceof Number n ? n.intValue() : 0;
        m.addAttribute("info",info);
        m.addAttribute("xp",xp);
        m.addAttribute("xpProgress",Math.min(xp, 100));
        m.addAttribute("session",us);
        return "profile/index";
    }

    @PostMapping("/profile/update")
    public String updateEmail(@RequestParam String email, HttpSession session, RedirectAttributes flash) {
        UserSession us = sessionUtils.get(session);
        if (us == null) {
            return "redirect:/login";
        }
        try {
            userClient.updateEmail(us.bearer(), us.getAccountId(), email);
            flash.addFlashAttribute("success", "Email обновлён");
        } catch (Exception e) { flash.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/profile";
    }
}
