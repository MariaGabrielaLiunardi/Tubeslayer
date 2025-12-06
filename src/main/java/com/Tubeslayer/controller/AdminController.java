package com.Tubeslayer.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.Tubeslayer.entity.MataKuliah; // Untuk Error 1
import com.Tubeslayer.entity.TugasBesar;
import com.Tubeslayer.entity.MataKuliahDosen; // <-- ADDED
import com.Tubeslayer.entity.MataKuliahMahasiswa;

import java.util.Collections; // <--- ADDED
import java.util.List; // <--- ADDED
import java.util.Map; // <--- ADDED
import java.util.stream.Collectors; // <--- ADDED
import java.util.Optional; // ADDED

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import com.Tubeslayer.repository.MataKuliahRepository;
import com.Tubeslayer.repository.TugasBesarRepository;
import com.Tubeslayer.repository.MataKuliahDosenRepository; // <-- ADDED
import com.Tubeslayer.repository.MataKuliahMahasiswaRepository;
import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardAdminService;

@Controller
public class AdminController {

    private final DashboardAdminService dashboardService;
    @Autowired private TugasBesarRepository tugasRepo;
    @Autowired private MataKuliahRepository mataKuliahRepo;

    @Autowired private MataKuliahDosenRepository mkDosenRepo; 
    @Autowired private MataKuliahMahasiswaRepository mkMahasiswaRepo;

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
        return "admin/menu-awal-ad"; // templates/admin/menu-awal-ad.html
    }

    @GetMapping("/admin/kelola-mata-kuliah")
    public String kelolaMataKuliah(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/kelola-mata-kuliah"; 
        // templates/admin/mata-kuliah.html
    }

    @GetMapping("/admin/kelola-dosen")
    public String kelolaDosen(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/kelola-dosen"; 
        // templates/admin/dosen.html
    }

    @GetMapping("/admin/kelola-mahasiswa")
    public String kelolaMahasiswa(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/kelola-mahasiswa"; 
        // templates/admin/mahasiswa.html
    }

    @GetMapping("/admin/arsip-matkul-detail")
public String kelolaArsipMatkulDetail(
    @RequestParam(required = false) String kodeMk,
    @AuthenticationPrincipal CustomUserDetails user, 
    Model model) {

    model.addAttribute("user", user);

    if (kodeMk == null || kodeMk.isEmpty()) {
        return "redirect:/admin/dashboard"; 
    }

    MataKuliah mkDetail = mataKuliahRepo.findById(kodeMk).orElse(null);
    if (mkDetail == null) {
        return "redirect:/admin/dashboard"; 
    }
    
    // ----------------------------------------------------
    // --- LOGIC TAMBAHAN: MENGAMBIL KOORDINATOR DOSEN ---
    // ----------------------------------------------------
    MataKuliahDosen koordinator = null;
    try {
        // Ambil dosen pertama yang mengajar MK ini
        List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, false); 
        if (!dosenList.isEmpty()) {
            koordinator = dosenList.get(0);
        }
    } catch (Exception e) {
        System.err.println("Error fetching coordinator: " + e.getMessage());
    }
    model.addAttribute("koordinator", koordinator); // <-- WAJIB: Mengirim ke Model
    // ----------------------------------------------------


    // 2. Ambil semua tugas besar yang TIDAK AKTIF (isActive = false)
    List<TugasBesar> tugasList = Collections.emptyList();
    try {
        tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, false); 
    } catch (Exception e) {
        System.err.println("Error fetching tasks for archive: " + e.getMessage());
    }
    
    // 3. Menghitung Metrik Tugas
   List<Map<String, Object>> tugasData = tugasList.stream().map(tugas -> {
        
        Map<String, Object> map = new java.util.HashMap<>(); 
        map.put("judulTugas", tugas.getJudulTugas());
        map.put("idTugas", tugas.getIdTugas()); // ID tetap berguna untuk navigasi detail tugas
        
        // Nilai default 0 tetap dipertahankan di Map (atau dihapus) untuk menjaga konsistensi Map
        // Jika Map tidak memerlukan field ini, Anda bisa menghapusnya, tapi lebih aman tetap ada:
        map.put("kelompokCount", 0); 
        map.put("submissionCount", 0);
        
        return map;
    }).collect(Collectors.toList());

    model.addAttribute("mkDetail", mkDetail);
    model.addAttribute("tugasDataList", tugasData);
    
    return "admin/arsip-matkul-detail"; 
    }


@GetMapping("/admin/matkul-peserta")
public String kelolaArsipMatkulPeserta(
    @RequestParam(required = false) String kodeMk,
    @AuthenticationPrincipal CustomUserDetails user,
    Model model) {

    model.addAttribute("user", user);

    if (kodeMk == null || kodeMk.isEmpty()) {
        // Redirect ke halaman list arsip MK (asumsi /admin/arsip-mata-kuliah)
        return "redirect:/admin/dashboard"; 
    }

    // 1. Ambil detail Mata Kuliah (hanya untuk tampilan)
    MataKuliah mk = mataKuliahRepo.findById(kodeMk).orElse(null);
    if (mk == null) {
        return "redirect:/admin/dashboard";
    }

    // --- LOGIC MENGAMBIL KOORDINATOR (Diperlukan untuk MK Header) ---
    // Koordinator harus diambil dari data MataKuliahDosen yang masih ada/terarsip
    MataKuliahDosen koordinator = null;
    try {
        // Asumsi: Kita ambil dosen pertama yang mengajar MK ini, terlepas dari status aktif dosen
        List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, false); 
        if (!dosenList.isEmpty()) {
            koordinator = dosenList.get(0);
        }
    } catch (Exception e) {
        System.err.println("Error fetching coordinator for archive: " + e.getMessage());
    }
    model.addAttribute("koordinator", koordinator); 
    // -------------------------------------------------------------------


    // 2. MENGAMBIL DAFTAR PESERTA ARSIP (Tanpa filter isActive pada relasi)
    List<MataKuliahMahasiswa> listPeserta = Collections.emptyList();
    
    if (mkMahasiswaRepo != null) {
        try {
            // Panggil method baru yang hanya memfilter berdasarkan KODE MK,
            // mengabaikan status isActive (karena ini arsip)
            listPeserta = mkMahasiswaRepo.findByMataKuliah_KodeMK(mk.getKodeMK()); 
        } catch (Exception e) {
            System.err.println("Error saat mengambil data peserta arsip: " + e.getMessage());
        }
    }
    
    model.addAttribute("mkDetail", mk);
    model.addAttribute("pesertaList", listPeserta); 

    return "admin/matkul-peserta"; 
}
}
