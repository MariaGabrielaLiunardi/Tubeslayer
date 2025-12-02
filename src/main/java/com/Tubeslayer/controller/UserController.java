package com.Tubeslayer.controller;

import com.Tubeslayer.service.AuthService;
import com.Tubeslayer.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import java.util.List;

// UserController.java (VERSI FINAL yang BENAR)
@Controller
public class UserController {

    @Autowired
    private AuthService authService; 

    // Login view: Hanya menampilkan halaman login.
    // Spring Security yang me-redirect jika user sudah terautentikasi.
    @GetMapping("/login")
    public String loginView() {
        return "login"; 
    }

    // Dashboard view: Hanya menampilkan halaman dashboard.
    // Spring Security yang mengamankan (hanya yang login bisa masuk).
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // Logout: (Boleh dipertahankan atau gunakan default Spring Security)
    /*
    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    */
}