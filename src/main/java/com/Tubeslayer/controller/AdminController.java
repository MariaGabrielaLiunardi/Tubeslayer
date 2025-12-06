package com.Tubeslayer.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardAdminService;

@Controller
public class AdminController {

    private final DashboardAdminService dashboardService;

    public AdminController(DashboardAdminService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ============================
    // DASHBOARD ADMIN
    // ============================
    @GetMapping("/admin/dashboard")
    public String adminDashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {

        model.addAttribute("user", user);

        LocalDate today = LocalDate.now();
        model.addAttribute("tanggal", today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

        int year = today.getYear();
        int month = today.getMonthValue();
        String semester = (month >= 7) ? year + "/" + (year + 1) : (year - 1) + "/" + year;

        model.addAttribute("semester", semester);

        long jumlahMk = dashboardService.getJumlahMkAktifUniversal();
        long jumlahTb = dashboardService.getJumlahTbAktifUniversal();

        model.addAttribute("jumlahMk", jumlahMk);
        model.addAttribute("jumlahTb", jumlahTb);

        return "admin/dashboard";
    }

    // ============================
    // MENU AWAL ADMIN
    // ============================
    @GetMapping("/admin/menu-awal-ad")
    public String menuAwalAdmin(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/menu-awal-ad";
    }

    // ============================
    // HALAMAN ADMIN LAINNYA
    // ============================
    @GetMapping("/admin/kelola-mata-kuliah")
    public String kelolaMataKuliah(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/kelola-mata-kuliah";
    }

    @GetMapping("/admin/kelola-dosen")
    public String kelolaDosen(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/kelola-dosen";
    }

    @GetMapping("/admin/kelola-mahasiswa")
    public String kelolaMahasiswa(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/kelola-mahasiswa";
    }
}
