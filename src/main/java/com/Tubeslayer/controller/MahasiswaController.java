package com.Tubeslayer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Tubeslayer.repository.MataKuliahRepository;
import com.Tubeslayer.repository.TugasBesarRepository;
import com.Tubeslayer.repository.jdbc.KelompokJdbcRepository.AnggotaKelompokDTO;
import com.Tubeslayer.repository.MataKuliahMahasiswaRepository;
import com.Tubeslayer.repository.MataKuliahDosenRepository; 

import java.util.List;
import java.util.Optional; 
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.Tubeslayer.entity.MataKuliah;
import com.Tubeslayer.entity.MataKuliahDosen; 
import com.Tubeslayer.entity.MataKuliahMahasiswa;
import com.Tubeslayer.entity.TugasBesar;
import com.Tubeslayer.dto.MahasiswaSearchDTO;
import com.Tubeslayer.repository.jdbc.KelompokJdbcRepository.AnggotaKelompokDTO;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;

import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardMahasiswaService;
import com.Tubeslayer.service.KelompokJdbcService;
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

    @Autowired
    private KelompokJdbcService kelompokJdbcService;

    private final DashboardMahasiswaService dashboardService;
    private final MataKuliahService mataKuliahService;

    public MahasiswaController(DashboardMahasiswaService dashboardService,
                               MataKuliahService mataKuliahService) {
        this.dashboardService = dashboardService;
        this.mataKuliahService = mataKuliahService;
    }

    @GetMapping("/mahasiswa/dashboard")
    public String mahasiswaDashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        System.out.println("Controller mahasiswaDashboard dipanggil!");
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

            List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
            if (!dosenList.isEmpty()) {
                koordinator = dosenList.get(0);
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinator for peserta: " + e.getMessage());
        }
        model.addAttribute("koordinator", koordinator); 


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

    @GetMapping("/mahasiswa/tugas-detail")
    public String tugasDetail(
            @RequestParam("idTugas") Integer idTugas,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {

        if (idTugas == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        Optional<TugasBesar> tugasOpt = tugasRepo.findById(idTugas);

        if (!tugasOpt.isPresent()) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        TugasBesar tugas = tugasOpt.get();
        MataKuliah mkDetail = tugas.getMataKuliah();

        MataKuliahDosen koordinator = null;
        try {
            List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(
                mkDetail.getKodeMK(), true);
            if (!dosenList.isEmpty()) {
                koordinator = dosenList.get(0);
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinator: " + e.getMessage());
        }

        // Data untuk kelola anggota menggunakan JDBC
        String modeKelompok = kelompokJdbcService.getModeKelompok(idTugas);
        boolean hasKelompok = kelompokJdbcService.hasKelompok(idTugas, user.getIdUser());
        boolean isLeader = kelompokJdbcService.isLeader(idTugas, user.getIdUser());
        boolean canManage = kelompokJdbcService.canManageAnggota(idTugas, user.getIdUser());

        int jumlahAnggota = kelompokJdbcService.countAnggota(idTugas, user.getIdUser());
        int maxAnggota = kelompokJdbcService.getMaxAnggota(idTugas);
        String namaKelompok = kelompokJdbcService.getNamaKelompok(idTugas, user.getIdUser());

        List<AnggotaKelompokDTO> anggotaList = Collections.emptyList();
        if (hasKelompok) {
            try {
                anggotaList = kelompokJdbcService.getAnggotaKelompok(idTugas, user.getIdUser());
            } catch (Exception e) {
                System.err.println("Error fetching anggota: " + e.getMessage());
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("tugas", tugas);
        model.addAttribute("mkDetail", mkDetail);
        model.addAttribute("koordinator", koordinator);

        model.addAttribute("modeKelompok", modeKelompok);
        model.addAttribute("hasKelompok", hasKelompok);
        model.addAttribute("isLeader", isLeader);
        model.addAttribute("canManageAnggota", canManage);

        model.addAttribute("jumlahAnggota", jumlahAnggota);
        model.addAttribute("maxAnggota", maxAnggota);
        
        model.addAttribute("namaKelompok", namaKelompok != null ? namaKelompok : "Belum ada kelompok");
        model.addAttribute("anggotaPreview", anggotaList);
        

        return "hlmn_tubes/hlmtubes";
    }

    // ============= JDBC API ENDPOINTS =============

    /**
     * API Endpoint untuk search mahasiswa
     * POST /mahasiswa/api/search-mahasiswa
     */
    @PostMapping("/mahasiswa/api/search-mahasiswa")
    @ResponseBody
    public ResponseEntity<?> searchMahasiswa(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal CustomUserDetails user) {

        try {
            // Validasi input
            if (!request.containsKey("idTugas") || !request.containsKey("keyword")) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("idTugas dan keyword harus diisi"));
            }

            Integer idTugas = Integer.parseInt(request.get("idTugas").toString());
            String keyword = request.get("keyword").toString().trim();

            if (keyword.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Keyword tidak boleh kosong"));
            }

            // Search menggunakan JDBC
            List<MahasiswaSearchDTO> results = 
                kelompokJdbcService.searchMahasiswa(idTugas, keyword);

            return ResponseEntity.ok(results);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Format idTugas tidak valid"));
        } catch (Exception e) {
            System.err.println("Error searching mahasiswa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Terjadi kesalahan saat mencari mahasiswa"));
        }
    }

    /**
     * API Endpoint untuk get anggota kelompok
     * GET /mahasiswa/api/anggota-kelompok?idTugas=1
     */
    @GetMapping("/mahasiswa/api/anggota-kelompok")
    @ResponseBody
    public ResponseEntity<?> getAnggotaKelompok(
            @RequestParam("idTugas") Integer idTugas,
            @AuthenticationPrincipal CustomUserDetails user) {

        try {
            List<AnggotaKelompokDTO> anggotaList = 
                kelompokJdbcService.getAnggotaKelompok(idTugas, user.getIdUser());

            return ResponseEntity.ok(anggotaList);

        } catch (Exception e) {
            System.err.println("Error getting anggota kelompok: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Terjadi kesalahan saat mengambil data anggota"));
        }
    }

    /**
     * API Endpoint untuk tambah anggota
     * POST /mahasiswa/api/tambah-anggota
     */
    @PostMapping("/mahasiswa/api/tambah-anggota")
    @ResponseBody
    public ResponseEntity<?> tambahAnggota(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal CustomUserDetails user) {

        try {
            // Validasi input
            if (!request.containsKey("idTugas") || !request.containsKey("idAnggota")) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("idTugas dan idAnggota harus diisi"));
            }

            Integer idTugas = Integer.parseInt(request.get("idTugas").toString());
            String idAnggota = request.get("idAnggota").toString();

            // Tambah anggota menggunakan JDBC
            kelompokJdbcService.tambahAnggota(idTugas, user.getIdUser(), idAnggota);

            // Hitung ulang jumlah anggota
            int jumlahAnggota = kelompokJdbcService.countAnggota(idTugas, user.getIdUser());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anggota berhasil ditambahkan");
            response.put("jumlahAnggota", jumlahAnggota);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error tambah anggota: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Terjadi kesalahan saat menambahkan anggota"));
        }
    }

    /**
     * API Endpoint untuk hapus anggota
     * POST /mahasiswa/api/hapus-anggota
     */
    @PostMapping("/mahasiswa/api/hapus-anggota")
    @ResponseBody
    public ResponseEntity<?> hapusAnggota(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal CustomUserDetails user) {

        try {
            // Validasi input
            if (!request.containsKey("idTugas") || !request.containsKey("idAnggota")) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("idTugas dan idAnggota harus diisi"));
            }

            Integer idTugas = Integer.parseInt(request.get("idTugas").toString());
            String idAnggota = request.get("idAnggota").toString();

            // Hapus anggota menggunakan JDBC
            kelompokJdbcService.hapusAnggota(idTugas, user.getIdUser(), idAnggota);

            // Hitung ulang jumlah anggota
            int jumlahAnggota = kelompokJdbcService.countAnggota(idTugas, user.getIdUser());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anggota berhasil dihapus");
            response.put("jumlahAnggota", jumlahAnggota);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error hapus anggota: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Terjadi kesalahan saat menghapus anggota"));
        }
    }

    /**
     * Helper method untuk create error response
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}