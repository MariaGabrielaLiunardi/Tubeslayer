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
import com.Tubeslayer.entity.*;

import com.Tubeslayer.repository.*;
import com.Tubeslayer.service.*;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
public class DosenController {

    @Autowired private MataKuliahRepository mataKuliahRepo;
    @Autowired private TugasBesarRepository tugasRepo;
    @Autowired private MataKuliahDosenRepository mkDosenRepo; 
    @Autowired(required = false) private RubrikNilaiRepository rubrikNilaiRepo;
    @Autowired(required = false) private MataKuliahMahasiswaRepository mkMahasiswaRepo; 

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
        String tahunAkademik = (today.getMonthValue() >= 7) ?
                year + "/" + (year + 1) :
                (year - 1) + "/" + year;
        model.addAttribute("semester", tahunAkademik);
      
        int jumlahMk = dashboardService.getJumlahMkAktif(user.getIdUser(), tahunAkademik);
        int jumlahTb = dashboardService.getJumlahTbAktif(user.getIdUser());
        model.addAttribute("jumlahMk", jumlahMk);
        model.addAttribute("jumlahTb", jumlahTb);

        List<MataKuliahDosen> listMK = mataKuliahService.getTop4ActiveByUserAndTahunAkademik(user.getIdUser(), tahunAkademik);
        model.addAttribute("mataKuliahDosenList", listMK);

        return "dosen/dashboard";
    }

 @GetMapping("/dosen/mata-kuliah")
public String listMK(@AuthenticationPrincipal CustomUserDetails user, Model model) {
    
    String idDosen = user.getIdUser(); 
    List<MataKuliahDosen> relasiMKDosen = mkDosenRepo.findById_IdUserAndIsActive(idDosen, true);
    
    model.addAttribute("mataKuliahDosenList", relasiMKDosen);
    model.addAttribute("user", user);

    LocalDate today = LocalDate.now();
    int year = today.getYear();
    
    // --- KOREKSI LOGIC PENGIRIMAN SEMESTER ---
    // Hitung string lengkap (misalnya, "Ganjil 2025/2026")
    String semesterPenuh = (today.getMonthValue() >= 9 || today.getMonthValue() <= 2) 
            ? "Ganjil " + year + "/" + (year + 1)
            : "Genap " + (year - 1) + "/" + year;
            
    // Kirim string lengkap ini ke Model
    model.addAttribute("semesterTahunAjaran", semesterPenuh); 
    
    // Kirim juga label Ganjil/Genap jika template membutuhkannya (meskipun sekarang tidak terpakai)
    String semesterLabel = (today.getMonthValue() >= 9 || today.getMonthValue() <= 2) ? "Ganjil" : "Genap";
    model.addAttribute("semesterLabel", semesterLabel);
    // ------------------------------------------

    return "dosen/mata-kuliah";
}

    @GetMapping("/dosen/matkul-detail")
    public String mkDetail(@RequestParam(required = false) String kodeMk, 
                           @AuthenticationPrincipal CustomUserDetails user, 
                           Model model) {
        if (kodeMk == null || kodeMk.isEmpty()) return "redirect:/dosen/mata-kuliah";

        MataKuliah mk = mataKuliahRepo.findById(kodeMk).orElse(null);
        if (mk == null) return "redirect:/dosen/mata-kuliah";

        model.addAttribute("user", user); 
        List<TugasBesar> tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(mk.getKodeMK(), true); 
        model.addAttribute("mkDetail", mk);
        model.addAttribute("tugasList", tugasList);

        return "dosen/matkul-detail";
    }

    @GetMapping("/dosen/matkul-peserta")
    public String peserta(@RequestParam(required = false) String kodeMk, 
                          @AuthenticationPrincipal CustomUserDetails user, 
                          Model model) {
        if (kodeMk == null || kodeMk.isEmpty()) return "redirect:/dosen/mata-kuliah";

        MataKuliah mk = mataKuliahRepo.findById(kodeMk).orElse(null);
        if (mk == null) return "redirect:/dosen/mata-kuliah";

        List<MataKuliahMahasiswa> listPeserta = Collections.emptyList();
        if (mkMahasiswaRepo != null) {
            try {
                listPeserta = mkMahasiswaRepo.findByMataKuliah_KodeMKAndIsActive(mk.getKodeMK(), true); 
            } catch (Exception e) {
                System.err.println("Error saat mengambil data peserta: " + e.getMessage());
            }
        }

        model.addAttribute("mkDetail", mk);
        model.addAttribute("user", user); 
        model.addAttribute("pesertaList", listPeserta);

        return "dosen/matkul-peserta";
    }

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
            
            if (request.getDeadline() != null && !request.getDeadline().isEmpty()) {
                LocalDate date = LocalDate.parse(request.getDeadline(), DateTimeFormatter.ISO_DATE); 
                tugasBaru.setDeadline(LocalDateTime.of(date, LocalTime.MAX)); 
            } else {
                 throw new Exception("Deadline tidak boleh kosong.");
            }
        
            tugasBaru.setMataKuliah(mk);         
            tugasBaru.setDosen(dosen); 
            tugasBaru.setMinAnggota(2);
            tugasBaru.setMaxAnggota(4);
            tugasBaru.setModeKel("Kelompok");
            tugasBaru.setStatus("Open"); 
            tugasBaru.setActive(true); 
            
            if (rubrikNilaiRepo == null) throw new Exception("Rubrik Nilai Repository tidak di-inject.");
            
            RubrikNilai newRubrik = new RubrikNilai();
            rubrikNilaiRepo.save(newRubrik);
            tugasBaru.setRubrik(newRubrik);

            tugasRepo.save(tugasBaru);

            return ResponseEntity.ok().body(Map.of(
                "message", "Tugas berhasil ditambahkan!", 
                "idTugas", tugasBaru.getIdTugas()
            ));
            
        } catch (Exception e) {
            System.err.println("Error saat menambah tugas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Gagal menambahkan tugas: " + e.getMessage()));
        }
    }
}
