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

@Controller
public class UserController {

    @Autowired
    private AuthService authService; 

    @GetMapping("/login")
    public String loginView() {
        return "login"; 
    }

    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
}