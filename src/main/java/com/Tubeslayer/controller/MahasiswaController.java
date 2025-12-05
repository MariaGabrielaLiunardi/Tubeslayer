package com.Tubeslayer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import com.Tubeslayer.repository.MataKuliahRepository;
import com.Tubeslayer.repository.TugasBesarRepository;
import com.Tubeslayer.repository.MataKuliahMahasiswaRepository;
import com.Tubeslayer.repository.MataKuliahDosenRepository; 

import java.util.List;
import java.util.Optional; 
import java.util.Collections;

import com.Tubeslayer.entity.MataKuliah;
import com.Tubeslayer.entity.MataKuliahDosen; 
import com.Tubeslayer.entity.MataKuliahMahasiswa;
import com.Tubeslayer.entity.TugasBesar;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;

import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardMahasiswaService;
import com.Tubeslayer.service.MataKuliahService;
import com.Tubeslayer.entity.MataKuliah;

import java.util.List; 

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class MahasiswaController {

    @Autowired
    private MataKuliahRepository mataKuliahRepo;

    @Autowired
    private TugasBesarRepository tugasRepo;

    @Autowired
    private MataKuliahMahasiswaRepository mkmRepo;
    
    @Autowired 
    private MataKuliahDosenRepository mkDosenRepo;

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
    public String mataKuliah(@AuthenticationPrincipal CustomUserDetails user, Model model) {

        String idMahasiswa = user.getIdUser(); 
        
        List<MataKuliahMahasiswa> enrollList =
            mkmRepo.findByUser_IdUserAndIsActive(idMahasiswa, true);

        model.addAttribute("enrollList", enrollList);
        model.addAttribute("user", user);

        // --- LOGIC SEMESTER (SALINAN DARI DASHBOARD) ---
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
        // --------------------------------------------------------

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

        // --- KOREKSI: LOGIC MENGAMBIL KOORDINATOR DOSEN ---
        MataKuliahDosen koordinator = null;
        
        try {
            // Ambil semua dosen yang mengajar MK ini
            List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
            if (!dosenList.isEmpty()) {
                // Ambil dosen pertama sebagai koordinator
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
            // Re-use logic for fetching coordinator
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
        
        // Asumsi: findByMataKuliah_KodeMKAndIsActive ada di MataKuliahMahasiswaRepository
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