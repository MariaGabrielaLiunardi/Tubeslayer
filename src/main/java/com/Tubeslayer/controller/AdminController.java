package com.Tubeslayer.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.Tubeslayer.dto.MKArchiveDTO;
import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardAdminService;
import com.Tubeslayer.entity.User;
import com.Tubeslayer.service.MataKuliahService;
import com.Tubeslayer.service.AuthService;
import com.Tubeslayer.repository.MataKuliahDosenRepository;

import java.util.List; 

@Controller
public class AdminController {

    private final DashboardAdminService dashboardService;
    private final MataKuliahService mataKuliahService;
    private final AuthService authService; 
    private final MataKuliahDosenRepository mataKuliahDosenRepo; 

    public AdminController(DashboardAdminService dashboardService, MataKuliahService mataKuliahService, AuthService authService, MataKuliahDosenRepository mataKuliahDosenRepo) {
        this.dashboardService = dashboardService;
        this.mataKuliahService = mataKuliahService;
        this.authService = authService; 
        this.mataKuliahDosenRepo = mataKuliahDosenRepo;  
    }

    // =====================================
    // Tambahkan ini → semua halaman admin dapat ${user}
    // =====================================
    @ModelAttribute("user")
    public User addLoggedUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUser() : null;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {

        LocalDate today = LocalDate.now();
        model.addAttribute("tanggal", today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

        int year = today.getYear();
        int month = today.getMonthValue();
        String semester = (month >= 7) ? year + "/" + (year + 1) : (year - 1) + "/" + year;

        model.addAttribute("semester", semester);

        model.addAttribute("jumlahMk", dashboardService.getJumlahMkAktifUniversal());
        model.addAttribute("jumlahTb", dashboardService.getJumlahTbAktifUniversal());
        model.addAttribute("jumlahDosen", dashboardService.getJumlahDosenAktif());
        model.addAttribute("jumlahMahasiswa", dashboardService.getJumlahMahasiswaAktif());

        return "admin/dashboard";
    }

    @GetMapping("/admin/menu-awal-ad")
    public String menuAwalAdmin() {
        return "admin/menu-awal-ad";
    }

    @GetMapping("/admin/kelola-mata-kuliah")
    public String kelolaMataKuliah() {
        return "admin/kelola-mata-kuliah";
    }

    @GetMapping("/admin/kelola-dosen")
    public String kelolaDosen() {
        return "admin/kelola-dosen";
    }

    @GetMapping("/admin/kelola-mahasiswa")
    public String kelolaMahasiswa() {
        return "admin/kelola-mahasiswa";
    }

    @GetMapping("/admin/arsip-mata-kuliah")
    public String getArsip(Model model) {
        List<MKArchiveDTO> list = mataKuliahDosenRepo.getArchiveMK();
        model.addAttribute("arsipMK", list);
        return "admin/arsip-mata-kuliah";
    }
}
