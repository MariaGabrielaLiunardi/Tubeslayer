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

import com.Tubeslayer.dto.PesertaMatkulDTO;
import com.Tubeslayer.dto.TugasBesarRequest;
import com.Tubeslayer.entity.*;

import com.Tubeslayer.repository.*;
import com.Tubeslayer.service.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Comparator; 

@Controller
public class DosenController {

    @Autowired private MataKuliahRepository mataKuliahRepo;
    @Autowired private TugasBesarRepository tugasRepo;
    @Autowired private MataKuliahDosenRepository mkDosenRepo; 
    @Autowired(required = false) private RubrikNilaiRepository rubrikNilaiRepo;
    @Autowired(required = false) private MataKuliahMahasiswaRepository mkMahasiswaRepo; 
    @Autowired(required = false) private KelompokRepository kelompokRepo;
    @Autowired(required = false) private TugasBesarKelompokRepository tugasKelompokRepo;
    @Autowired(required = false) private UserKelompokRepository userKelompokRepo;
    @Autowired(required = false) private UserRepository userRepo;

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

        listMK.sort(Comparator.comparing(mk -> mk.getMataKuliah().getNama()));

        int gradientCount = 4; // misal ada 4 gradient
        for (MataKuliahDosen mkDosen : listMK) {
            int colorIndex = Math.abs(mkDosen.getMataKuliah().getKodeMK().hashCode()) % gradientCount;
            mkDosen.setColorIndex(colorIndex); // ini menentukan gradient konsisten
        }

        model.addAttribute("mataKuliahDosenList", listMK);

        return "dosen/dashboard";
    }

 @GetMapping("/dosen/mata-kuliah")
public String listMK(@AuthenticationPrincipal CustomUserDetails user, Model model) {
    
    String idDosen = user.getIdUser(); 
    List<MataKuliahDosen> relasiMKDosen = mkDosenRepo.findById_IdUserAndIsActive(idDosen, true);

    relasiMKDosen.sort(Comparator.comparing(mk -> mk.getMataKuliah().getNama()));
    
    model.addAttribute("mataKuliahDosenList", relasiMKDosen);
    model.addAttribute("user", user);

    LocalDate today = LocalDate.now();
    int year = today.getYear();

    String semesterPenuh = (today.getMonthValue() >= 9 || today.getMonthValue() <= 2) 
            ? "Ganjil " + year + "/" + (year + 1)
            : "Genap " + (year - 1) + "/" + year;
            
    model.addAttribute("semesterTahunAjaran", semesterPenuh); 
    
    String semesterLabel = (today.getMonthValue() >= 9 || today.getMonthValue() <= 2) ? "Ganjil" : "Genap";
    model.addAttribute("semesterLabel", semesterLabel);

    return "dosen/mata-kuliah";
}

    @GetMapping("/dosen/matkul-detail")
    public String matkulDetail(@RequestParam String kodeMk,
                            @AuthenticationPrincipal CustomUserDetails user,
                            Model model) {

        model.addAttribute("user", user);

        // Ambil data mata kuliah
        MataKuliah mkDetail = mataKuliahRepo.findById(kodeMk)
                .orElseThrow(() -> new IllegalArgumentException("Mata Kuliah tidak ditemukan"));

        // Gradient konsisten berdasarkan kodeMK
        int gradientCount = 4; // jumlah gradient yang kamu punya
        int colorIndex = Math.abs(kodeMk.hashCode()) % gradientCount;

        model.addAttribute("mkDetail", mkDetail);
        model.addAttribute("colorIndex", colorIndex);

        // Ambil list tugas dsb...
        List<TugasBesar> tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
        model.addAttribute("tugasList", tugasList);

        return "dosen/matkul-detail";
    }

    @GetMapping("/dosen/tugas-detail")
    public String tugasDetail(@RequestParam(required = false) Integer idTugas,
                              @AuthenticationPrincipal CustomUserDetails user,
                              Model model) {
        if (idTugas == null) return "redirect:/dosen/mata-kuliah";

        TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
        if (tugas == null) return "redirect:/dosen/mata-kuliah";

        // Data tugas
        model.addAttribute("tugas", tugas);
        model.addAttribute("user", user);
        model.addAttribute("mkDetail", tugas.getMataKuliah());

        // Format deadline dengan locale Indonesia
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        String deadlineFormatted = tugas.getDeadline().format(formatter);
        model.addAttribute("deadlineFormatted", deadlineFormatted);

        // Tentukan penentu kelompok berdasarkan mode_kel
        String penentuKelompok = "Dosen".equalsIgnoreCase(tugas.getModeKel()) ? "Dosen" : "Mahasiswa";
        model.addAttribute("penentuKelompok", penentuKelompok);

        return "hlmn_tubes/hlmtubes-dosen";
    }

    @GetMapping("/dosen/matkul-peserta")
    public String peserta(@RequestParam(required = false) String kodeMk,
                         @RequestParam(required = false) Integer colorIndex, 
                      @AuthenticationPrincipal CustomUserDetails user, 
                      Model model) {
    if (kodeMk == null || kodeMk.isEmpty()) return "redirect:/dosen/mata-kuliah";

    MataKuliah mk = mataKuliahRepo.findById(kodeMk).orElse(null);
    if (mk == null) return "redirect:/dosen/mata-kuliah";

    // 1. Color Index 
    int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
    model.addAttribute("colorIndex", finalColorIndex);

    // 2. Ambil List Mahasiswa
    List<MataKuliahMahasiswa> listPeserta = Collections.emptyList();
    if (mkMahasiswaRepo != null) {
        try {
            listPeserta = mkMahasiswaRepo.findByMataKuliah_KodeMKAndIsActive(mk.getKodeMK(), true); 
        } catch (Exception e) {
            System.err.println("Error saat mengambil data peserta: " + e.getMessage());
        }
    }
    
    // 3. Ambil Koordinator Dosen
    User koordinator = user.getUser(); 

    // --- 4. GABUNGKAN DOSEN DAN MAHASISWA MENGGUNAKAN DTO ---
    List<PesertaMatkulDTO> combinedList = new ArrayList<>();
    int counter = 1;

    // 4.1. Tambahkan Dosen Koordinator 
    combinedList.add(new PesertaMatkulDTO(
        counter++, 
        koordinator.getNama(), 
        koordinator.getIdUser(), 
        "Koordinator"
    ));
    
    // 4.2. Konversi Mahasiswa ke DTO
    List<PesertaMatkulDTO> mahasiswaDTOs = listPeserta.stream()
        .map(rel -> new PesertaMatkulDTO(
            0, // Index akan diupdate setelah sorting
            rel.getUser().getNama(),
            rel.getUser().getIdUser(),
            "Mahasiswa",
            rel.getKelas()
        ))
        .collect(Collectors.toList());
        
    // 4.3. Urutkan Mahasiswa berdasarkan Nama
    mahasiswaDTOs.sort(Comparator.comparing(PesertaMatkulDTO::getNama));

    // 4.4. Gabungkan dan update nomor urut
    combinedList.addAll(mahasiswaDTOs);
    for (int i = 1; i < combinedList.size(); i++) {
        combinedList.get(i).setNo(counter++);
    }
    // -------------------------------------------------------------

    model.addAttribute("mkDetail", mk);
    model.addAttribute("user", user); 

    if (combinedList == null) {
    combinedList = Collections.emptyList();
    }

    model.addAttribute("combinedPesertaList", combinedList);
    model.addAttribute("combinedPesertaList", combinedList); // List baru untuk tabel
    model.addAttribute("pesertaCount", listPeserta.size()); // Jumlah yang hanya Mahasiswa

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

     @GetMapping("/api/kelompok/tugas/{idTugas}")
    @ResponseBody
    public ResponseEntity<?> getKelompokByTugas(@PathVariable Integer idTugas) {
        try {
            // Cek apakah tugas ada
            TugasBesar tugas = tugasRepo.findById(idTugas)
                .orElseThrow(() -> new Exception("Tugas tidak ditemukan"));

            // Ambil semua relasi tugas_besar_kelompok untuk tugas ini
            List<TugasBesarKelompok> tugasKelompokList = tugasKelompokRepo.findByIdTugas(idTugas);
            
            // Build response dengan detail kelompok dari database
            List<Map<String, Object>> kelompokDetailList = tugasKelompokList.stream()
                .map(tk -> {
                    Kelompok kelompok = tk.getKelompok();
                    Map<String, Object> detail = new HashMap<>();
                    
                    detail.put("id_kelompok", kelompok.getIdKelompok());
                    detail.put("nama_kelompok", kelompok.getNamaKelompok());
                    detail.put("id_tugas", idTugas);
                    
                    // Ambil semua anggota kelompok dari user_kelompok
                    List<UserKelompok> anggotaList = userKelompokRepo.findByKelompok_IdKelompok(kelompok.getIdKelompok());
                    detail.put("jumlah_anggota", anggotaList.size());
                    
                    // Cari ketua kelompok (role = 'leader')
                    UserKelompok ketua = anggotaList.stream()
                        .filter(uk -> "leader".equalsIgnoreCase(uk.getRole()))
                        .findFirst()
                        .orElse(null);
                    
                    if (ketua != null) {
                        detail.put("nama_ketua", ketua.getUser().getNama());
                        detail.put("id_user_ketua", ketua.getUser().getIdUser());
                    } else {
                        detail.put("nama_ketua", "N/A");
                        detail.put("id_user_ketua", null);
                    }
                    
                    detail.put("max_anggota", tugas.getMaxAnggota());
                    detail.put("min_anggota", tugas.getMinAnggota());
                    
                    return detail;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(kelompokDetailList);
            
        } catch (Exception e) {
            System.err.println("Error getting kelompok: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST - Buat kelompok baru dan simpan ke database
     */
    @PostMapping("/api/kelompok")
    @ResponseBody
    public ResponseEntity<?> createKelompok(@RequestBody Map<String, Object> request) {
        try {
            Integer idTugas = (Integer) request.get("id_tugas");
            String namaKelompok = (String) request.get("nama_kelompok");
            String idUserKetua = (String) request.get("id_user_ketua");

            // Validasi input
            if (idTugas == null || namaKelompok == null || idUserKetua == null) {
                throw new Exception("Data tidak lengkap");
            }

            // Cek apakah tugas ada di database
            TugasBesar tugas = tugasRepo.findById(idTugas)
                .orElseThrow(() -> new Exception("Tugas tidak ditemukan"));

            // Cek apakah user ketua ada di database
            User ketua = userRepo.findById(idUserKetua)
                .orElseThrow(() -> new Exception("User tidak ditemukan"));

            // Validasi role user harus mahasiswa
            if (!"Mahasiswa".equalsIgnoreCase(ketua.getRole())) {
                throw new Exception("Ketua kelompok harus seorang mahasiswa");
            }

            // 1. Buat kelompok baru di tabel kelompok
            Kelompok kelompokBaru = new Kelompok();
            kelompokBaru.setNamaKelompok(namaKelompok);
            kelompokRepo.save(kelompokBaru);

            // 2. Hubungkan kelompok dengan tugas di tabel tugas_besar_kelompok
            TugasBesarKelompok tugasKelompok = new TugasBesarKelompok();
            tugasKelompok.setIdKelompok(kelompokBaru.getIdKelompok());
            tugasKelompok.setIdTugas(idTugas);
            tugasKelompok.setKelompok(kelompokBaru);
            tugasKelompok.setTugas(tugas);
            tugasKelompokRepo.save(tugasKelompok);

            // 3. Tambahkan ketua ke kelompok di tabel user_kelompok
            UserKelompok userKelompok = new UserKelompok();
            userKelompok.setUser(ketua);
            userKelompok.setKelompok(kelompokBaru);
            userKelompok.setRole("leader");
            userKelompok.set_active(true);
            userKelompokRepo.save(userKelompok);

            Map<String, Object> response = new HashMap<>();
            response.put("id_kelompok", kelompokBaru.getIdKelompok());
            response.put("nama_kelompok", namaKelompok);
            response.put("message", "Kelompok berhasil dibuat");

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error creating kelompok: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST - Hapus kelompok dari database (cascade delete)
     */
    @PostMapping("/api/kelompok/{idKelompok}")
    @ResponseBody
    public ResponseEntity<?> deleteKelompok(@PathVariable Integer idKelompok) {
        try {
            // Cek apakah kelompok ada di database
            Kelompok kelompok = kelompokRepo.findById(idKelompok)
                .orElseThrow(() -> new Exception("Kelompok tidak ditemukan"));

            // 1. Hapus relasi user_kelompok terlebih dahulu
            List<UserKelompok> userKelompokList = userKelompokRepo.findByKelompok_IdKelompok(idKelompok);
            if (!userKelompokList.isEmpty()) {
                userKelompokRepo.deleteAll(userKelompokList);
            }

            // 2. Hapus relasi tugas_besar_kelompok
            List<TugasBesarKelompok> tugasKelompokList = tugasKelompokRepo.findByIdKelompok(idKelompok);
            if (!tugasKelompokList.isEmpty()) {
                tugasKelompokRepo.deleteAll(tugasKelompokList);
            }

            // 3. Hapus kelompok dari tabel kelompok
            kelompokRepo.delete(kelompok);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Kelompok berhasil dihapus"
            ));
            
        } catch (Exception e) {
            System.err.println("Error deleting kelompok: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST - Finalisasi kelompok untuk tugas (update status di database)
     */
    @PostMapping("/api/kelompok/finalisasi/{idTugas}")
    @ResponseBody
    public ResponseEntity<?> finalisasiKelompok(@PathVariable Integer idTugas) {
        try {
            // Cek apakah tugas ada di database
            TugasBesar tugas = tugasRepo.findById(idTugas)
                .orElseThrow(() -> new Exception("Tugas tidak ditemukan"));

            // Update status tugas menjadi "Finalized"
            tugas.setStatus("Finalized");
            tugasRepo.save(tugas);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Kelompok berhasil difinalisasi"
            ));
            
        } catch (Exception e) {
            System.err.println("Error finalizing kelompok: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET - Search mahasiswa by ID atau nama dari database
     */
    @GetMapping("/api/mahasiswa/search")
    @ResponseBody
    public ResponseEntity<?> searchMahasiswa(@RequestParam String q) {
        try {
            // Search mahasiswa dari database berdasarkan nama atau ID
            // Query: WHERE role = 'Mahasiswa' AND (nama LIKE '%q%' OR id_user LIKE '%q%')
            List<User> results = userRepo.findByRoleAndNamaContainingIgnoreCaseOrRoleAndIdUserContaining(
                "Mahasiswa", q, "Mahasiswa", q
            );

            // Format response
            List<Map<String, String>> response = results.stream()
                .limit(10) // Limit hasil pencarian
                .map(user -> {
                    Map<String, String> userMap = new HashMap<>();
                    userMap.put("id_user", user.getIdUser());
                    // KOREKSI: Ini sepertinya salah, harusnya user.getNama()
                    userMap.put("nama", user.getNama()); 
                    userMap.put("email", user.getEmail());
                    return userMap;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error searching mahasiswa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET - Get detail tugas besar dari database
     */
    @GetMapping("/api/tugas/{idTugas}")
    @ResponseBody
    public ResponseEntity<?> getTugas(@PathVariable Integer idTugas) {
        try {
            TugasBesar tugas = tugasRepo.findById(idTugas)
                .orElseThrow(() -> new Exception("Tugas tidak ditemukan"));

            Map<String, Object> response = new HashMap<>();
            response.put("id_tugas", tugas.getIdTugas());
            response.put("judul_tugas", tugas.getJudulTugas());
            response.put("deskripsi", tugas.getDeskripsi());
            response.put("max_anggota", tugas.getMaxAnggota());
            response.put("min_anggota", tugas.getMinAnggota());
            response.put("deadline", tugas.getDeadline().toString());
            response.put("status", tugas.getStatus());
            response.put("mode_kel", tugas.getModeKel());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error getting tugas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/api/kelompok/{idKelompok}/anggota")
    @ResponseBody
    public ResponseEntity<?> getAnggotaKelompok(@PathVariable Integer idKelompok) {
        try {
            // Cek apakah kelompok ada
            Kelompok kelompok = kelompokRepo.findById(idKelompok)
                .orElseThrow(() -> new Exception("Kelompok tidak ditemukan"));

            // Ambil semua anggota kelompok
            List<UserKelompok> anggotaList = userKelompokRepo.findByKelompok_IdKelompok(idKelompok);

            // Format response
            List<Map<String, Object>> response = anggotaList.stream()
                .filter(uk -> uk.is_active()) // Filter hanya yang aktif
                .map(uk -> {
                    Map<String, Object> anggota = new HashMap<>();
                    anggota.put("idUser", uk.getUser().getIdUser());
                    anggota.put("nama", uk.getUser().getNama());
                    anggota.put("email", uk.getUser().getEmail());
                    anggota.put("role", uk.getRole());
                    return anggota;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error getting anggota: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST - Tambah anggota ke kelompok
     */
    @PostMapping("/api/kelompok/tambah-anggota")
    @ResponseBody
    public ResponseEntity<?> tambahAnggota(@RequestBody Map<String, Object> request) {
        try {
            Integer idKelompok = (Integer) request.get("idKelompok");
            String idAnggota = (String) request.get("idAnggota");

            // Validasi
            if (idKelompok == null || idAnggota == null) {
                throw new Exception("Data tidak lengkap");
            }

            // Cek kelompok exist
            Kelompok kelompok = kelompokRepo.findById(idKelompok)
                .orElseThrow(() -> new Exception("Kelompok tidak ditemukan"));

            // Cek user exist
            User user = userRepo.findById(idAnggota)
                .orElseThrow(() -> new Exception("User tidak ditemukan"));

            // Cek apakah user sudah ada di kelompok
            List<UserKelompok> existing = userKelompokRepo.findByKelompok_IdKelompok(idKelompok);
            boolean alreadyMember = existing.stream()
                .anyMatch(uk -> uk.getUser().getIdUser().equals(idAnggota) && uk.is_active());

            if (alreadyMember) {
                throw new Exception("User sudah menjadi anggota kelompok");
            }

            // Cek max anggota
            long currentCount = existing.stream().filter(uk -> uk.is_active()).count();
            
            // Get max anggota dari tugas
            List<TugasBesarKelompok> tbkList = tugasKelompokRepo.findByIdKelompok(idKelompok);
            if (!tbkList.isEmpty()) {
                TugasBesar tugas = tbkList.get(0).getTugas();
                if (currentCount >= tugas.getMaxAnggota()) {
                    throw new Exception("Kelompok sudah penuh");
                }
            }

            // Tambah anggota
            UserKelompok newMember = new UserKelompok();
            newMember.setUser(user);
            newMember.setKelompok(kelompok);
            newMember.setRole("member");
            newMember.set_active(true);
            userKelompokRepo.save(newMember);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Anggota berhasil ditambahkan"
            ));

        } catch (Exception e) {
            System.err.println("Error adding member: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST - Hapus anggota dari kelompok
     */
    @PostMapping("/api/kelompok/hapus-anggota")
    @ResponseBody
    public ResponseEntity<?> hapusAnggota(@RequestBody Map<String, Object> request) {
        try {
            Integer idKelompok = (Integer) request.get("idKelompok");
            String idAnggota = (String) request.get("idAnggota");

            // Validasi
            if (idKelompok == null || idAnggota == null) {
                throw new Exception("Data tidak lengkap");
            }

            // Cek kelompok exist
            kelompokRepo.findById(idKelompok)
                .orElseThrow(() -> new Exception("Kelompok tidak ditemukan"));

            // Cek user exist
            User user = userRepo.findById(idAnggota)
                .orElseThrow(() -> new Exception("User tidak ditemukan"));

            // Cari user_kelompok entry
            List<UserKelompok> members = userKelompokRepo.findByKelompok_IdKelompok(idKelompok);
            UserKelompok toRemove = members.stream()
                .filter(uk -> uk.getUser().getIdUser().equals(idAnggota) && uk.is_active())
                .findFirst()
                .orElseThrow(() -> new Exception("User bukan anggota kelompok"));

            // Tidak boleh hapus leader
            if ("leader".equalsIgnoreCase(toRemove.getRole())) {
                throw new Exception("Tidak dapat menghapus ketua kelompok");
            }

            // Soft delete
            toRemove.set_active(false);
            userKelompokRepo.save(toRemove);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Anggota berhasil dihapus"
            ));

        } catch (Exception e) {
            System.err.println("Error removing member: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}