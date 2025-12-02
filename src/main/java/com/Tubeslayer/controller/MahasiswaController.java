package com.Tubeslayer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MahasiswaController {

    @GetMapping("/mahasiswa/dashboard")
    public String mahasiswaDashboard() {
        return "mahasiswa/dashboard"; // templates/mahasiswa/dashboard.html
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