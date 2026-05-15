package com.task.tracker.web.controller;

import com.task.tracker.web.service.UserSession;
import com.task.tracker.web.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {
    private final SessionUtils sessionUtils;
    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("currentUri", request.getRequestURI());
    }

    @ModelAttribute
    public void addAttributes(Model model, HttpSession session) {
        UserSession us = sessionUtils.get(session);
        if (us != null) {
            model.addAttribute("session",  us);
            model.addAttribute("isAdmin",  us.isAdmin());
        }
    }
}
