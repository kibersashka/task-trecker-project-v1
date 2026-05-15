package com.task.tracker.web.controller;


import com.task.tracker.web.client.AuthClient;
import com.task.tracker.web.dto.LoginForm;
import com.task.tracker.web.dto.RegisterForm;
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


@Controller @RequiredArgsConstructor
public class AuthController {

    private final AuthClient auth;
    private final SessionUtils sessionUtils;


    @GetMapping("/")
    public String root(HttpSession session) {
        return sessionUtils.ok(session) ? "redirect:/tasks" : "redirect:/home";
    }

    @GetMapping("/login")
    public String loginPage(Model m, HttpSession session) {
        if (sessionUtils.ok(session)){
            return "redirect:/tasks";
        }
        m.addAttribute("loginForm", new LoginForm());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult br,
                        HttpSession session, Model m) {
        if (br.hasErrors()) {
            return "auth/login";
        }
        try {
            String token = auth.login(form);
            UserSession us = new UserSession();
            us.setAccessToken(token);
            us.setAccountId(sessionUtils.extractId(token));
            us.setUsername(sessionUtils.extractUsername(token));
            us.setRole(sessionUtils.extractRole(token));
            sessionUtils.save(session, us);
            return "redirect:/tasks";
        } catch (Exception e) {
            m.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model m) {
        m.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterForm form, BindingResult br,
                           Model m, RedirectAttributes flash) {
        if (br.hasErrors()) {
            return "auth/register";
        }
        try {
            auth.register(form);
            flash.addFlashAttribute("success", "Аккаунт создан! Войдите.");
            return "redirect:/login";
        } catch (Exception e) {
            m.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        sessionUtils.clear(session);
        return "redirect:/home";
    }
}
