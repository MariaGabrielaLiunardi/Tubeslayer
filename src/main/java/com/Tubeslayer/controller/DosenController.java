package com.Tubeslayer.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import com.Tubeslayer.repository.MataKuliahDosenRepository;
import com.Tubeslayer.repository.TugasBesarRepository;
import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardService;
import com.Tubeslayer.entity.User;

@Controller
public class DosenController {

    private final DashboardService dashboardService;

    public DosenController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dosen/dashboard")
    public String dosenDashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
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

        // panggil service
        int jumlahMk = dashboardService.getJumlahMkAktif(user.getIdUser(), semester);
        int jumlahTb = dashboardService.getJumlahTbAktif(user.getIdUser());

        model.addAttribute("jumlahMk", jumlahMk);
        model.addAttribute("jumlahTb", jumlahTb);

        return "dosen/dashboard"; // ⬅ sesuai lokasi file
    }

    // 1. Mapping untuk halaman daftar semua mata kuliah
    @GetMapping("/dosen/mata-kuliah")
    public String dosenMataKuliahList() {
        return "dosen/mata-kuliah";
    }

    // 2. Mapping untuk halaman detail mata kuliah (Kuliah)
    @GetMapping("/dosen/matkul-detail") 
    public String dosenMatkulDetail() {
        return "dosen/matkul-detail"; 
    }

    // 3. Mapping untuk halaman daftar peserta
    @GetMapping("/dosen/matkul-peserta")
    public String dosenMatkulPeserta() {
        return "dosen/matkul-peserta"; 
    }
}
