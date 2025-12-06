package com.Tubeslayer.controller;

import com.Tubeslayer.repository.MataKuliahRepository;
import com.Tubeslayer.repository.TugasBesarRepository;
import com.Tubeslayer.repository.MataKuliahMahasiswaRepository;
import com.Tubeslayer.repository.MataKuliahDosenRepository; 
import com.Tubeslayer.entity.MataKuliahDosen; 
import com.Tubeslayer.entity.MataKuliahMahasiswa;
import com.Tubeslayer.entity.TugasBesar;
import com.Tubeslayer.entity.MataKuliah; // Import yang benar

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardMahasiswaService;
import com.Tubeslayer.service.MataKuliahService;

import java.util.List;
import java.util.Collections;
import java.util.Optional; 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors; // Tambah import ini untuk MataKuliahDosen::getMataKuliah

@Controller
public class MahasiswaController {

    // --- DEPENDENSI MENGGUNAKAN FINAL ---
    private final MataKuliahRepository mataKuliahRepo;
    private final TugasBesarRepository tugasRepo;
    private final MataKuliahMahasiswaRepository mkmRepo;
    private final MataKuliahDosenRepository mkDosenRepo;
    private final DashboardMahasiswaService dashboardService;
    private final MataKuliahService mataKuliahService;

    // --- CONSTRUCTOR INJECTION ---
    public MahasiswaController(MataKuliahRepository mataKuliahRepo,
                               TugasBesarRepository tugasRepo,
                               MataKuliahMahasiswaRepository mkmRepo,
                               MataKuliahDosenRepository mkDosenRepo,
                               DashboardMahasiswaService dashboardService,
                               MataKuliahService mataKuliahService) {
        this.mataKuliahRepo = mataKuliahRepo;
        this.tugasRepo = tugasRepo;
        this.mkmRepo = mkmRepo;
        this.mkDosenRepo = mkDosenRepo;
        this.dashboardService = dashboardService;
        this.mataKuliahService = mataKuliahService;
    }
    // ------------------------------------
    
    // --- UTILITY LOGIC (Memastikan kode tidak terulang) ---
    private void addSemesterAttributes(Model model) {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        String semesterTahunAjaran;
        String semesterLabel;

        semesterTahunAjaran = (today.getMonthValue() >= 7) ?
                year + "/" + (year + 1) :
                (year - 1) + "/" + year;
        
        if (today.getMonthValue() >= 9 || today.getMonthValue() <= 2) { 
            semesterLabel = "Ganjil"; 
        } else {
            semesterLabel = "Genap";
        }
        
        model.addAttribute("semesterTahunAjaran", semesterTahunAjaran);
        model.addAttribute("semesterLabel", semesterLabel);

        // Tambahan untuk DashboardService
        String tahunAkademik;
        if (today.getMonthValue() >= 7) {
            tahunAkademik = year + "/" + (year + 1);
        } else {
            tahunAkademik = (year - 1) + "/" + year;
        }
        model.addAttribute("semester", tahunAkademik);
    }
    // ----------------------------------------------------

    @GetMapping("/mahasiswa/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        
        LocalDate today = LocalDate.now();
        model.addAttribute("tanggal", today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        
        addSemesterAttributes(model); // Panggil utility untuk semester

        String tahunAkademik = (String) model.getAttribute("semester");
        
        // Logika dashboardService
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
    public String mataKuliah(@AuthenticationPrincipal CustomUserDetails user, Model model) {

        String idMahasiswa = user.getIdUser(); 
        
        List<MataKuliahMahasiswa> enrollList =
            mkmRepo.findByUser_IdUserAndIsActive(idMahasiswa, true);

        model.addAttribute("enrollList", enrollList);
        model.addAttribute("user", user);

        addSemesterAttributes(model); // Panggil utility untuk semester

        return "mahasiswa/mata-kuliah";
    }


    @GetMapping("/mahasiswa/matkul-detail")
    public String detailMatkul(
            @RequestParam("mk") String kodeMk,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {

        if (kodeMk == null || kodeMk.isEmpty()) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        MataKuliah mkDetail = mataKuliahRepo.findById(kodeMk).orElse(null); 
        
        if (mkDetail == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        // --- LOGIC MENGAMBIL KOORDINATOR DOSEN ---
        MataKuliahDosen koordinator = null;
        
        try {
            List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
            if (!dosenList.isEmpty()) {
                koordinator = dosenList.get(0); 
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinator: " + e.getMessage());
        }

        model.addAttribute("koordinator", koordinator); 
        // ----------------------------------------------------

        List<TugasBesar> tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(mkDetail.getKodeMK(), true);

        model.addAttribute("mkDetail", mkDetail);
        model.addAttribute("tugasList", tugasList);
        model.addAttribute("user", user); 

        return "mahasiswa/matkul-detail";
    }


    @GetMapping("/mahasiswa/matkul-peserta")
    public String peserta(@RequestParam(required = false) String kodeMk, 
                          @AuthenticationPrincipal CustomUserDetails user, 
                          Model model) {
         
        if (kodeMk == null || kodeMk.isEmpty()) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        MataKuliah mk = mataKuliahRepo.findById(kodeMk).orElse(null);

        if (mk == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        // --- 1. MENGAMBIL DATA KOORDINATOR ---
        MataKuliahDosen koordinator = null;
        try {
            List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
            if (!dosenList.isEmpty()) {
                koordinator = dosenList.get(0);
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinator for peserta: " + e.getMessage());
        }
        model.addAttribute("koordinator", koordinator); 
        // -------------------------------------------------------------------


        // --- 2. MENGAMBIL DATA PESERTA ---
        List<MataKuliahMahasiswa> listPeserta = Collections.emptyList();
        
        if (mkmRepo != null) {
            try {
                listPeserta = mkmRepo.findByMataKuliah_KodeMKAndIsActive(mk.getKodeMK(), true); 
            } catch (Exception e) {
                System.err.println("Error saat mengambil data peserta: " + e.getMessage());
            }
        }
        
        model.addAttribute("mkDetail", mk);
        model.addAttribute("user", user); 
        model.addAttribute("pesertaList", listPeserta); 

        return "mahasiswa/matkul-peserta";
    }
}