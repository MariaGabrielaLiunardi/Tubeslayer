package com.Tubeslayer.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Tubeslayer.service.CustomUserDetails;

@Controller
public class AdminController {

    @GetMapping("/admin/dashboard")
    public String adminDashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);

        // ambil tanggal sekarang
        LocalDate today = LocalDate.now();
        model.addAttribute("tanggal", today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

        // hitung semester
        int year = today.getYear();
        int month = today.getMonthValue();
        String semester;

        if (month >= 7) {
            semester = year + "/" + (year + 1);   // contoh: 2025/2026
        } else {
            semester = (year - 1) + "/" + year;   // contoh: 2024/2025
        }
        model.addAttribute("semester", semester);
        return "admin/dashboard";
    }
}
