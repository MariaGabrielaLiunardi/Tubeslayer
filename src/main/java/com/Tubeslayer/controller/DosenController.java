package com.Tubeslayer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import com.Tubeslayer.dto.TugasBesarRequest;

import java.util.List;
import java.util.Map;
import java.util.Collections; // Tambah
import java.util.stream.Collectors;

import com.Tubeslayer.entity.MataKuliahDosen;
import com.Tubeslayer.entity.User;
import com.Tubeslayer.entity.RubrikNilai;
import com.Tubeslayer.entity.MataKuliahMahasiswa; // Entitas Peserta

import com.Tubeslayer.repository.MataKuliahDosenRepository;
import com.Tubeslayer.repository.MataKuliahRepository;
import com.Tubeslayer.repository.RubrikNilaiRepository;
import com.Tubeslayer.repository.TugasBesarRepository;
import com.Tubeslayer.repository.MataKuliahMahasiswaRepository; // REPOSITORY PESERTA

import com.Tubeslayer.entity.MataKuliah;
import com.Tubeslayer.entity.TugasBesar;
import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardDosenService;
import com.Tubeslayer.entity.MataKuliahDosen;
import com.Tubeslayer.entity.User;
import com.Tubeslayer.service.MataKuliahService;

import java.util.List; 

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Controller
public class DosenController {

    @Autowired
    private MataKuliahRepository mataKuliahRepo;

    @Autowired
    private TugasBesarRepository tugasRepo;

    @Autowired
    private MataKuliahDosenRepository mkDosenRepo; 

    @Autowired(required = false) 
    private RubrikNilaiRepository rubrikNilaiRepo;
    
    @Autowired(required = false)
    private MataKuliahMahasiswaRepository mkMahasiswaRepo; 

    @GetMapping("/dosen/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
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
        String semester = (today.getMonthValue() >= 7) ?
                year + "/" + (year + 1) :
                (year - 1) + "/" + year;
        model.addAttribute("semester", semester);
      
        int jumlahMk = dashboardService.getJumlahMkAktif(user.getIdUser(), tahunAkademik);
        int jumlahTb = dashboardService.getJumlahTbAktif(user.getIdUser());
        model.addAttribute("jumlahMk", jumlahMk);
        model.addAttribute("jumlahTb", jumlahTb);

        // ambil list MK aktif dosen
        List<MataKuliahDosen> listMK = mataKuliahService.getTop4ActiveByUserAndTahunAkademik(user.getIdUser(), tahunAkademik);
        model.addAttribute("mataKuliahDosenList", listMK);

        return "dosen/dashboard";
    }

    @GetMapping("/dosen/mata-kuliah")
    public String listMK(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        
        String idDosen = user.getIdUser(); 
        
        List<MataKuliahDosen> relasiMKDosen = mkDosenRepo.findById_IdUserAndIsActive(idDosen, true);
        
        List<MataKuliah> listMK = relasiMKDosen.stream()
            .map(MataKuliahDosen::getMataKuliah)
            .collect(Collectors.toList());
            
        model.addAttribute("mataKuliahDosenList", relasiMKDosen);
        model.addAttribute("user", user);

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        String semester;
        if (today.getMonthValue() >= 9 || today.getMonthValue() <= 2) { 
            semester = "Ganjil " + year + "/" + (year + 1);
        } else {
            semester = "Genap " + (year - 1) + "/" + year;
        }
        model.addAttribute("semesterTahunAjaran", semester); 

        return "dosen/mata-kuliah";
    }

    @GetMapping("/dosen/matkul-detail")
    public String mkDetail(@RequestParam(required = false) String kodeMk, 
                           @AuthenticationPrincipal CustomUserDetails user, 
                           Model model) {
        
        if (kodeMk == null || kodeMk.isEmpty()) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliah mk = mataKuliahRepo.findById(kodeMk).orElse(null);
        if (mk == null) {
            return "redirect:/dosen/mata-kuliah";
        }

        model.addAttribute("user", user); 

        // Kirim detail MK dan Tugas
        List<TugasBesar> tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(mk.getKodeMK(), true); 

        model.addAttribute("mkDetail", mk);
        model.addAttribute("tugasList", tugasList);

        return "dosen/matkul-detail";
    }

    @GetMapping("/dosen/matkul-peserta")
    public String peserta(@RequestParam(required = false) String kodeMk, 
                          @AuthenticationPrincipal CustomUserDetails user, 
                          Model model) {
         
        if (kodeMk == null || kodeMk.isEmpty()) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliah mk = mataKuliahRepo.findById(kodeMk).orElse(null);

        if (mk == null) {
            return "redirect:/dosen/mata-kuliah";
        }

        // --- KOREKSI: LOGIC PENGAMBILAN DATA PESERTA ---
        List<MataKuliahMahasiswa> listPeserta = Collections.emptyList();
        
        if (mkMahasiswaRepo != null) {
            try {
                // Panggil method query yang sudah ditambahkan di repository
                listPeserta = mkMahasiswaRepo.findByMataKuliah_KodeMKAndIsActive(mk.getKodeMK(), true); 
            } catch (Exception e) {
                // Tangani error jika terjadi (misalnya, mapping JPA salah)
                System.err.println("Error saat mengambil data peserta: " + e.getMessage());
            }
        }

        model.addAttribute("mkDetail", mk);
        model.addAttribute("user", user); 
        model.addAttribute("pesertaList", listPeserta); // Data yang akan ditampilkan di Thymeleaf

        return "dosen/matkul-peserta";
    }

    // ------------------------------------------------------------------
    // ENDPOINT API UNTUK MENAMBAH TUGAS (POST)
    // ------------------------------------------------------------------

    @PostMapping("/api/dosen/matakuliah/{kodeMk}/tugas")
    @ResponseBody 
    public ResponseEntity<?> tambahTugas(@PathVariable String kodeMk, 
                                          @RequestBody TugasBesarRequest request,
                                          @AuthenticationPrincipal CustomUserDetails user) {
        try {
            User dosen = user.getUser(); 
            
            MataKuliah mk = mataKuliahRepo.findById(kodeMk)
                            .orElseThrow(() -> new Exception("Mata Kuliah tidak ditemukan."));

            TugasBesar tugasBaru = new TugasBesar();
            tugasBaru.setJudulTugas(request.getJudulTugas()); 
            tugasBaru.setDeskripsi(request.getDeskripsi());
            
            // --- KONVERSI DEADLINE ---
            if (request.getDeadline() != null && !request.getDeadline().isEmpty()) {
                LocalDate date = LocalDate.parse(request.getDeadline(), DateTimeFormatter.ISO_DATE); 
                tugasBaru.setDeadline(LocalDateTime.of(date, LocalTime.MAX)); 
            } else {
                 throw new Exception("Deadline tidak boleh kosong.");
            }
        
            // 3. Set Foreign Keys dan default values
            tugasBaru.setMataKuliah(mk);         
            tugasBaru.setDosen(dosen); 
            
            // --- KOREKSI UNTUK MINIMUM 2 ANGGOTA ---
            tugasBaru.setMinAnggota(2); // Minimum 2 anggota
            tugasBaru.setMaxAnggota(4); // Maksimum disesuaikan (Contoh: 4)
            tugasBaru.setModeKel("Kelompok"); // Mode diubah menjadi Kelompok
            // ----------------------------------------

            tugasBaru.setStatus("Open"); 
            tugasBaru.setActive(true); 
            
            // Mengatasi Error id_rubrik cannot be null
            if (rubrikNilaiRepo == null) {
                 throw new Exception("Rubrik Nilai Repository tidak di-inject.");
            }
            
            RubrikNilai defaultRubrik = rubrikNilaiRepo.findById(1)
                .orElseThrow(() -> new Exception("Rubrik Nilai default (ID 1) tidak ditemukan."));
            tugasBaru.setRubrik(defaultRubrik);

            // 4. Simpan ke Database
            tugasRepo.save(tugasBaru);

            return ResponseEntity.ok().body(Map.of("message", "Tugas berhasil ditambahkan!", "idTugas", tugasBaru.getIdTugas()));
            
        } catch (Exception e) {
            System.err.println("Error saat menambah tugas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Gagal menambahkan tugas: " + e.getMessage()));
        }

    }
}