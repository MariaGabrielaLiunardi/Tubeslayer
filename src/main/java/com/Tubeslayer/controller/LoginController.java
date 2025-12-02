package com.Tubeslayer.controller;

import com.Tubeslayer.service.AuthService;
import com.Tubeslayer.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginView(HttpSession session) {
        if (session.getAttribute("user") != null) {
            // Jika sudah login, langsung ke dashboard
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(String email, String password,
                        HttpSession session, Model model) {
        try {
            User user = authService.login(email, password);

            // Simpan user ke session
            session.setAttribute("user", user);

            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // Jika belum login, redirect ke login
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
