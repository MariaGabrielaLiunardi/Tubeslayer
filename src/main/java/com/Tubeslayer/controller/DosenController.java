package com.Tubeslayer.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import com.Tubeslayer.repository.MataKuliahDosenRepository;
import com.Tubeslayer.repository.TugasBesarRepository;
import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardDosenService;
import com.Tubeslayer.entity.MataKuliahDosen;
import com.Tubeslayer.entity.User;
import com.Tubeslayer.service.MataKuliahService;

import java.util.List; 

@Controller
public class DosenController {

    private final DashboardDosenService dashboardService;
    private final MataKuliahService mataKuliahService;

    public DosenController(DashboardDosenService dashboardService, MataKuliahService mataKuliahService) {
        this.dashboardService = dashboardService;
        this.mataKuliahService = mataKuliahService;
    }

    @GetMapping("/dosen/dashboard")
    public String dosenDashboard(@AuthenticationPrincipal CustomUserDetails user, Model model, HttpSession session) {
        model.addAttribute("user", user);

        LocalDate today = LocalDate.now();
        model.addAttribute("tanggal", today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

        int year = today.getYear();
        int month = today.getMonthValue();
        String tahunAkademik = (month >= 7) ? year + "/" + (year + 1) : (year - 1) + "/" + year;
        model.addAttribute("semester", tahunAkademik);

        int jumlahMk = dashboardService.getJumlahMkAktif(user.getIdUser(), tahunAkademik);
        int jumlahTb = dashboardService.getJumlahTbAktif(user.getIdUser());
        model.addAttribute("jumlahMk", jumlahMk);
        model.addAttribute("jumlahTb", jumlahTb);

        // ambil list MK aktif dosen
        List<MataKuliahDosen> listMK = mataKuliahService.getTop4ActiveByUserAndTahunAkademik(user.getIdUser(), tahunAkademik);
        model.addAttribute("mataKuliahDosenList", listMK);

        return "dosen/dashboard";
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
