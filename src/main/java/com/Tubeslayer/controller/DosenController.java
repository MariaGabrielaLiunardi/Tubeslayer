package com.Tubeslayer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DosenController {

    @GetMapping("/dosen/dashboard")
    public String dosenDashboard() {
        return "dosen/dashboard";  // templates/dosen/dashboard.html
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
