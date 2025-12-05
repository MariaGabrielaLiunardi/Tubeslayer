package com.Tubeslayer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;

import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardMahasiswaService;
import com.Tubeslayer.service.MataKuliahService;
import com.Tubeslayer.entity.MataKuliah;

import java.util.List; 

@Controller
public class MahasiswaController {

    private final DashboardMahasiswaService dashboardService;
    private final MataKuliahService mataKuliahService;

    public MahasiswaController(DashboardMahasiswaService dashboardService,
                               MataKuliahService mataKuliahService) {
        this.dashboardService = dashboardService;
        this.mataKuliahService = mataKuliahService;
    }

    @GetMapping("/mahasiswa/dashboard")
    public String mahasiswaDashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);

        LocalDate today = LocalDate.now();
        model.addAttribute("tanggal", today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

        int year = today.getYear();
        int month = today.getMonthValue();
        String tahunAkademik;
        if (month >= 7) {
            tahunAkademik = year + "/" + (year + 1);
        } else {
            tahunAkademik = (year - 1) + "/" + year;
        }
        model.addAttribute("semester", tahunAkademik);

        int jumlahMk = dashboardService.getJumlahMkAktif(user.getIdUser(), tahunAkademik);
        int jumlahTb = dashboardService.getJumlahTbAktif(user.getIdUser());
        model.addAttribute("jumlahMk", jumlahMk);
        model.addAttribute("jumlahTb", jumlahTb);

        List<MataKuliah> listMK = mataKuliahService.getActiveByMahasiswaAndTahunAkademik(user.getIdUser(), tahunAkademik);
        model.addAttribute("mataKuliahList", listMK);

        return "mahasiswa/dashboard";
    }

    // 1. Mapping untuk halaman daftar semua mata kuliah
    @GetMapping("/mahasiswa/mata-kuliah")
    public String mahasiswaMataKuliahList() {
        // 1. Ambil data Mata Kuliah dari DB
       // List<MataKuliah> listMK = mataKuliahRepository.findAll(); 
    
        // 2. Tambahkan list ke objek Model, agar bisa diakses di Thymeleaf
        //model.addAttribute("mataKuliahList", listMK);
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

    @GetMapping("/hlmn_tubes/hlmtubes")
    public String halamanTubes() {
        return "/hlmn_tubes/hlmtubes"; 
    }
}