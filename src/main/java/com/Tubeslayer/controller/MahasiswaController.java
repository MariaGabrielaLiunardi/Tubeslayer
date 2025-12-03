package com.Tubeslayer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;

import com.Tubeslayer.service.CustomUserDetails;

@Controller
public class MahasiswaController {

    @GetMapping("/mahasiswa/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
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
        return "mahasiswa/dashboard"; // file HTML kamu
    }
    // 1. Mapping untuk halaman daftar semua mata kuliah
    @GetMapping("/mahasiswa/mata-kuliah")
    public String mahasiswaMataKuliahList() {
        return "mahasiswa/mata-kuliah";
    }

    // 2. Mapping untuk halaman detail mata kuliah (Kuliah)
    @GetMapping("/mahasiswa/matkul-detail") 
    public String mahasiswaMatkulDetail() {
        // template location: templates/Matakuliah/matkul-detail.html
        return "mahasiswa/matkul-detail"; 
    }

    // 3. Mapping untuk halaman daftar peserta
    // Template: templates/Matakuliah/matkul-peserta.html
    @GetMapping("/mahasiswa/matkul-peserta")
    public String mahasiswaMatkulPeserta() {
        return "mahasiswa/matkul-peserta"; 
    }
}