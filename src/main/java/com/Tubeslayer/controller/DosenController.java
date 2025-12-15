package com.Tubeslayer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.Tubeslayer.dto.PesertaMatkulDTO;
import com.Tubeslayer.dto.TugasBesarRequest;
import com.Tubeslayer.entity.*;

import com.Tubeslayer.repository.*;
import com.Tubeslayer.service.*;
import com.Tubeslayer.dto.PemberianNilaiPerKomponenDTO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Comparator; 

@Controller
public class DosenController {

    private static final Logger logger = LoggerFactory.getLogger(DosenController.class);

    @Autowired private MataKuliahRepository mataKuliahRepo;
    @Autowired private TugasBesarRepository tugasRepo;
    @Autowired private MataKuliahDosenRepository mkDosenRepo; 
    @Autowired private RubrikNilaiRepository rubrikNilaiRepo;
    @Autowired private KomponenNilaiRepository komponenNilaiRepo;
    @Autowired private MataKuliahMahasiswaRepository mkMahasiswaRepo; 
    @Autowired private KelompokRepository kelompokRepo;
    @Autowired private TugasBesarKelompokRepository tugasKelompokRepo;
    @Autowired private UserKelompokRepository userKelompokRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private NilaiRepository nilaiRepository;
    @Autowired private NilaiKomponenRepository nilaiKomponenRepository;
    @Autowired private NilaiService nilaiService;

    private final DashboardDosenService dashboardService;

    public DosenController(DashboardDosenService dashboardService, MataKuliahService mataKuliahService) {
        this.dashboardService = dashboardService;
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

        List<MataKuliahDosen> mataKuliahDosenList = mkDosenRepo.findById_IdUserAndIsActive(user.getIdUser(), true);
        
        logger.info("Dashboard Dosen - User ID: {}, Tahun Akademik: {}, Mata Kuliah Found: {}", 
                    user.getIdUser(), tahunAkademik, mataKuliahDosenList.size());
        if (!mataKuliahDosenList.isEmpty()) {
            mataKuliahDosenList.forEach(mk -> logger.info("  - {}: {} (tahunAkademik: {})", 
                mk.getMataKuliah().getKodeMK(), mk.getMataKuliah().getNama(), mk.getTahunAkademik()));
        }

        int gradientCount = 4;
        for (MataKuliahDosen mkd : mataKuliahDosenList) {
            String kodeMK = mkd.getMataKuliah().getKodeMK();
            
            int colorIndex = Math.abs(kodeMK.hashCode()) % gradientCount;
            mkd.setColorIndex(colorIndex);
        }
        List<MataKuliahDosen> limitedList = mataKuliahDosenList.stream()
            .limit(4) 
            .collect(Collectors.toList());

        model.addAttribute("mataKuliahDosenList", limitedList);

        return "dosen/dashboard";
    }

 @GetMapping("/dosen/mata-kuliah")
    public String mataKuliah(@AuthenticationPrincipal CustomUserDetails user, Model model) {
    
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        String semesterTahunAjaran = (today.getMonthValue() >= 7) ? year + "/" + (year + 1) : (year - 1) + "/" + year;
        String semesterLabel = (today.getMonthValue() >= 9 || today.getMonthValue() <= 2) ? "Ganjil" : "Genap";
    
        List<MataKuliahDosen> mataKuliahDosenList = mkDosenRepo.findById_IdUserAndIsActive(user.getIdUser(), true);

        int gradientCount = 4;
        for (MataKuliahDosen mkd : mataKuliahDosenList) {
            String kodeMK = mkd.getMataKuliah().getKodeMK();
            
            int colorIndex = Math.abs(kodeMK.hashCode()) % gradientCount;
            
            mkd.setColorIndex(colorIndex);
        }

        mataKuliahDosenList.sort(Comparator.comparing(mk -> mk.getMataKuliah().getNama()));
    
        model.addAttribute("mataKuliahDosenList", mataKuliahDosenList);
        model.addAttribute("user", user);
        model.addAttribute("semesterTahunAjaran", semesterTahunAjaran);
        model.addAttribute("semesterLabel", semesterLabel);

        return "dosen/mata-kuliah";
    }

    @GetMapping("/dosen/matkul-detail")
    public String matkulDetail(@RequestParam String kodeMk,
                           @AuthenticationPrincipal CustomUserDetails user, 
                           Model model) {

        model.addAttribute("user", user); 

        MataKuliah mkDetail = mataKuliahRepo.findById(kodeMk)
                .orElseThrow(() -> new IllegalArgumentException("Mata Kuliah tidak ditemukan"));

        int gradientCount = 4; 
        int colorIndex = Math.abs(kodeMk.hashCode()) % gradientCount;

        model.addAttribute("mkDetail", mkDetail);
        model.addAttribute("colorIndex", colorIndex);

        List<TugasBesar> tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
        tugasList.sort(Comparator.comparing(TugasBesar::getDeadline)); 

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

        model.addAttribute("tugas", tugas);
        model.addAttribute("user", user);
        model.addAttribute("mkDetail", tugas.getMataKuliah());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        String deadlineFormatted = tugas.getDeadline().format(formatter);
        model.addAttribute("deadlineFormatted", deadlineFormatted);

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

    int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
    model.addAttribute("colorIndex", finalColorIndex);

    List<MataKuliahDosen> dosenMatkulList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);

    List<MataKuliahMahasiswa> listPeserta = Collections.emptyList();
    if (mkMahasiswaRepo != null) {
        try {
            listPeserta = mkMahasiswaRepo.findByMataKuliah_KodeMKAndIsActive(mk.getKodeMK(), true); 
        } catch (Exception e) {
            System.err.println("Error saat mengambil data peserta: " + e.getMessage());
        }
    }
    
    List<PesertaMatkulDTO> combinedList = new ArrayList<>();
    int counter = 1;
    
    String loggedInUserId = user.getIdUser(); 

    List<PesertaMatkulDTO> dosenDTOs = dosenMatkulList.stream()
        .map(rel -> {
            String role;
            
            if (rel.getUser().getIdUser().equals(loggedInUserId)) {
                role = "Koordinator"; 
            } else {
                role = "Pengampu"; 
            }
            
            return new PesertaMatkulDTO(
                0, 
                rel.getUser().getNama(),
                rel.getUser().getIdUser(),
                role
            );
        })
        .collect(Collectors.toList());
        
    dosenDTOs.sort(Comparator
        .comparing((PesertaMatkulDTO dto) -> dto.getRole().equals("Koordinator")).reversed()
        .thenComparing(PesertaMatkulDTO::getNama)
    );
    
    for (PesertaMatkulDTO dto : dosenDTOs) {
        dto.setNo(counter++);
        combinedList.add(dto);
    }
    
    List<PesertaMatkulDTO> mahasiswaDTOs = listPeserta.stream()
        .map(rel -> new PesertaMatkulDTO(
            0, 
            rel.getUser().getNama(),
            rel.getUser().getIdUser(),
            "Mahasiswa",
            rel.getKelas()
        ))
        .collect(Collectors.toList());
        
    mahasiswaDTOs.sort(Comparator.comparing(PesertaMatkulDTO::getNama));

    for (PesertaMatkulDTO dto : mahasiswaDTOs) {
        dto.setNo(counter++);
        combinedList.add(dto);
    }
    
    MataKuliahDosen koordinatorUntukHeader = dosenMatkulList.stream()
        .filter(mkd -> mkd.getUser().getIdUser().equals(loggedInUserId)) 
        .findFirst()
        .orElse(null);
        
    model.addAttribute("koordinator", koordinatorUntukHeader); 
    model.addAttribute("mkDetail", mk);
    model.addAttribute("user", user); 
    
    model.addAttribute("combinedPesertaList", combinedList); 
    model.addAttribute("pesertaCount", listPeserta.size()); 

    return "dosen/matkul-peserta";
}

    @GetMapping("/api/kelompok/tugas/{idTugas}")
    @ResponseBody
    public ResponseEntity<?> getKelompokByTugas(@PathVariable Integer idTugas) {
        try {
            
            TugasBesar tugas = tugasRepo.findById(idTugas)
                .orElseThrow(() -> new Exception("Tugas tidak ditemukan"));

            List<TugasBesarKelompok> tugasKelompokList = tugasKelompokRepo.findByIdTugas(idTugas);
            
            List<Map<String, Object>> kelompokDetailList = tugasKelompokList.stream()
                .map(tk -> {
                    Kelompok kelompok = tk.getKelompok();
                    Map<String, Object> detail = new HashMap<>();
                    
                    detail.put("id_kelompok", kelompok.getIdKelompok());
                    detail.put("nama_kelompok", kelompok.getNamaKelompok());
                    detail.put("id_tugas", idTugas);
                    
                    List<UserKelompok> anggotaList = userKelompokRepo.findByKelompok_IdKelompok(kelompok.getIdKelompok());
                    detail.put("jumlah_anggota", anggotaList.size());
                    
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

    @PostMapping("/api/kelompok")
    @ResponseBody
    public ResponseEntity<?> createKelompok(@RequestBody Map<String, Object> request) {
        try {
            Integer idTugas = (Integer) request.get("id_tugas");
            String namaKelompok = (String) request.get("nama_kelompok");
            String idUserKetua = (String) request.get("id_user_ketua");

            if (idTugas == null || namaKelompok == null || idUserKetua == null) {
                throw new Exception("Data tidak lengkap");
            }

            TugasBesar tugas = tugasRepo.findById(idTugas)
                .orElseThrow(() -> new Exception("Tugas tidak ditemukan"));

            User ketua = userRepo.findById(idUserKetua)
                .orElseThrow(() -> new Exception("User tidak ditemukan"));

            if (!"Mahasiswa".equalsIgnoreCase(ketua.getRole())) {
                throw new Exception("Ketua kelompok harus seorang mahasiswa");
            }

            Kelompok kelompokBaru = new Kelompok();
            kelompokBaru.setNamaKelompok(namaKelompok);
            kelompokRepo.save(kelompokBaru);

            TugasBesarKelompok tugasKelompok = new TugasBesarKelompok();
            tugasKelompok.setIdKelompok(kelompokBaru.getIdKelompok());
            tugasKelompok.setIdTugas(idTugas);
            tugasKelompok.setKelompok(kelompokBaru);
            tugasKelompok.setTugas(tugas);
            tugasKelompokRepo.save(tugasKelompok);

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

    @PostMapping("/api/kelompok/{idKelompok}")
    @ResponseBody
    public ResponseEntity<?> deleteKelompok(@PathVariable Integer idKelompok) {
        try {
            
            Kelompok kelompok = kelompokRepo.findById(idKelompok)
                .orElseThrow(() -> new Exception("Kelompok tidak ditemukan"));

            List<UserKelompok> userKelompokList = userKelompokRepo.findByKelompok_IdKelompok(idKelompok);
            if (!userKelompokList.isEmpty()) {
                userKelompokRepo.deleteAll(userKelompokList);
            }

            List<TugasBesarKelompok> tugasKelompokList = tugasKelompokRepo.findByIdKelompok(idKelompok);
            if (!tugasKelompokList.isEmpty()) {
                tugasKelompokRepo.deleteAll(tugasKelompokList);
            }

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

    @PostMapping("/api/kelompok/finalisasi/{idTugas}")
    @ResponseBody
    public ResponseEntity<?> finalisasiKelompok(@PathVariable Integer idTugas) {
        try {
            
            TugasBesar tugas = tugasRepo.findById(idTugas)
                .orElseThrow(() -> new Exception("Tugas tidak ditemukan"));

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

    @GetMapping("/api/mahasiswa/search")
    @ResponseBody
    public ResponseEntity<?> searchMahasiswa(@RequestParam String q) {
        try {
            
            List<User> results = userRepo.findByRoleAndNamaContainingIgnoreCaseOrRoleAndIdUserContaining(
                "Mahasiswa", q, "Mahasiswa", q
            );

            List<Map<String, String>> response = results.stream()
                .limit(10) 
                .map(user -> {
                    Map<String, String> userMap = new HashMap<>();
                    userMap.put("id_user", user.getIdUser());
                    
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

            List<UserKelompok> anggotaList = userKelompokRepo.findByKelompok_IdKelompok(idKelompok);

            List<Map<String, Object>> response = anggotaList.stream()
                .filter(uk -> uk.is_active()) 
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
        
        String modeKelRequest = request.getModeKel(); 
        
        if (modeKelRequest == null || modeKelRequest.isEmpty()) {
            
            tugasBaru.setModeKel("Kelompok"); 
        } else if (modeKelRequest.equalsIgnoreCase("Dosen") || 
                   modeKelRequest.equalsIgnoreCase("Mahasiswa")) {
            
            tugasBaru.setModeKel(modeKelRequest); 
        } else {
            
            throw new Exception("Mode penentuan anggota tidak valid.");
        }
        
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

    @PostMapping("/api/kelompok/tambah-anggota")
    @ResponseBody
    public ResponseEntity<?> tambahAnggota(@RequestBody Map<String, Object> request) {
        try {
            Integer idKelompok = (Integer) request.get("idKelompok");
            String idAnggota = (String) request.get("idAnggota");

            if (idKelompok == null || idAnggota == null) {
                throw new Exception("Data tidak lengkap");
            }

            Kelompok kelompok = kelompokRepo.findById(idKelompok)
                .orElseThrow(() -> new Exception("Kelompok tidak ditemukan"));

            User user = userRepo.findById(idAnggota)
                .orElseThrow(() -> new Exception("User tidak ditemukan"));

            List<UserKelompok> existing = userKelompokRepo.findByKelompok_IdKelompok(idKelompok);
            boolean alreadyMember = existing.stream()
                .anyMatch(uk -> uk.getUser().getIdUser().equals(idAnggota) && uk.is_active());

            if (alreadyMember) {
                throw new Exception("User sudah menjadi anggota kelompok");
            }

            long currentCount = existing.stream().filter(uk -> uk.is_active()).count();
            
            List<TugasBesarKelompok> tbkList = tugasKelompokRepo.findByIdKelompok(idKelompok);
            if (!tbkList.isEmpty()) {
                TugasBesar tugas = tbkList.get(0).getTugas();
                if (currentCount >= tugas.getMaxAnggota()) {
                    throw new Exception("Kelompok sudah penuh");
                }
            }

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

    @PostMapping("/api/kelompok/hapus-anggota")
    @ResponseBody
    public ResponseEntity<?> hapusAnggota(@RequestBody Map<String, Object> request) {
        try {
            Integer idKelompok = (Integer) request.get("idKelompok");
            String idAnggota = (String) request.get("idAnggota");

            if (idKelompok == null || idAnggota == null) {
                throw new Exception("Data tidak lengkap");
            }

            kelompokRepo.findById(idKelompok)
                .orElseThrow(() -> new Exception("Kelompok tidak ditemukan"));

            List<UserKelompok> members = userKelompokRepo.findByKelompok_IdKelompok(idKelompok);
            UserKelompok toRemove = members.stream()
                .filter(uk -> uk.getUser().getIdUser().equals(idAnggota) && uk.is_active())
                .findFirst()
                .orElseThrow(() -> new Exception("User bukan anggota kelompok"));

            if ("leader".equalsIgnoreCase(toRemove.getRole())) {
                throw new Exception("Tidak dapat menghapus ketua kelompok");
            }

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

    @GetMapping("/dosen/nilai")
    public String dosenNilai(
            @RequestParam(required = false) String kodeMk,
            @RequestParam(required = false) Integer colorIndex,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        
        if (kodeMk == null || kodeMk.isEmpty()) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(kodeMk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliahDosen mkDosen = mkDosenRepo.findById_IdUserAndKodeMK(user.getIdUser(), kodeMk);
        if (mkDosen == null || !mkDosen.isActive()) {
            return "redirect:/dosen/mata-kuliah";
        }

        List<TugasBesar> tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
        tugasList.sort(Comparator.comparing(TugasBesar::getDeadline));

        List<MataKuliahMahasiswa> pesertaList = mkMahasiswaRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
        
        model.addAttribute("user", user);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("mkDosen", mkDosen);
        model.addAttribute("tugasList", tugasList);
        model.addAttribute("pesertaList", pesertaList);
        model.addAttribute("kodeMk", kodeMk);
        model.addAttribute("colorIndex", finalColorIndex);

        return "nilai/Dosen/nilai-dosen";
    }

    @GetMapping("/dosen/jadwal-penilaian")
    public String dosenJadwalPenilaian(
            @RequestParam(required = false) String kodeMk,
            @RequestParam(required = false) Integer idTugas,
            @RequestParam(required = false) Integer colorIndex,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        
        if (kodeMk == null || kodeMk.isEmpty()) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(kodeMk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliahDosen mkDosen = mkDosenRepo.findById_IdUserAndKodeMK(user.getIdUser(), kodeMk);
        if (mkDosen == null || !mkDosen.isActive()) {
            return "redirect:/dosen/mata-kuliah";
        }

        if (idTugas == null) {
            List<TugasBesar> tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
            if (!tugasList.isEmpty()) {
                tugasList.sort(Comparator.comparing(TugasBesar::getDeadline));
                return "redirect:/dosen/jadwal-penilaian?kodeMk=" + kodeMk + "&idTugas=" + tugasList.get(0).getIdTugas();
            }
            return "redirect:/dosen/mata-kuliah";
        }

        TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
        if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(kodeMk) || !tugas.isActive()) {
            return "redirect:/dosen/mata-kuliah";
        }

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
        
        model.addAttribute("user", user);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("tugas", tugas);
        model.addAttribute("kodeMk", kodeMk);
        model.addAttribute("idTugas", idTugas);
        model.addAttribute("colorIndex", finalColorIndex);

        return "nilai/Dosen/jadwal-penilaian-dosen";
    }

    @GetMapping("/dosen/jadwal-penilaian/get")
    @ResponseBody
    public Map<String, Object> getJadwalPenilaian(
            @RequestParam(required = false) String kodeMk,
            @RequestParam(required = false) Integer idTugas,
            @AuthenticationPrincipal CustomUserDetails user) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            
            if (kodeMk == null || kodeMk.isEmpty() || idTugas == null) {
                response.put("success", false);
                response.put("message", "Parameter tidak valid");
                return response;
            }

            MataKuliah mataKuliah = mataKuliahRepo.findById(kodeMk).orElse(null);
            if (mataKuliah == null) {
                response.put("success", false);
                response.put("message", "Mata kuliah tidak ditemukan");
                return response;
            }

            MataKuliahDosen mkDosen = mkDosenRepo.findById_IdUserAndKodeMK(user.getIdUser(), kodeMk);
            if (mkDosen == null || !mkDosen.isActive()) {
                response.put("success", false);
                response.put("message", "Anda tidak berhak mengakses mata kuliah ini");
                return response;
            }

            TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
            if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(kodeMk) || !tugas.isActive()) {
                response.put("success", false);
                response.put("message", "Tugas tidak ditemukan");
                return response;
            }

            RubrikNilai rubrik = tugas.getRubrik();
            List<Map<String, Object>> jadwalList = new ArrayList<>();

            if (rubrik != null && rubrik.getKomponenList() != null) {
                for (KomponenNilai komponen : rubrik.getKomponenList()) {
                    Map<String, Object> jadwal = new HashMap<>();
                    jadwal.put("idTugas", komponen.getIdKomponen());
                    jadwal.put("judulTugas", komponen.getNamaKomponen());
                    jadwal.put("deadline", tugas.getDeadline().toString());
                    jadwalList.add(jadwal);
                }
            }

            response.put("success", true);
            response.put("jadwalList", jadwalList);
            
        } catch (Exception e) {
            System.err.println("Error getting jadwal: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Gagal mengambil data jadwal: " + e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/dosen/jadwal-penilaian/save")
    @ResponseBody
    public Map<String, Object> saveJadwalPenilaian(
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal CustomUserDetails user) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String kodeMk = (String) payload.get("kodeMk");
            Object idTugasObj = payload.get("idTugas");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> jadwalList = (List<Map<String, Object>>) payload.get("jadwalList");
            @SuppressWarnings("unchecked")
            List<Integer> deletedKomponenIds = (List<Integer>) payload.get("deletedTugasIds");

            if (kodeMk == null || kodeMk.isEmpty() || idTugasObj == null) {
                response.put("success", false);
                response.put("message", "Parameter tidak valid");
                return response;
            }

            Integer idTugas = ((Number) idTugasObj).intValue();

            MataKuliah mataKuliah = mataKuliahRepo.findById(kodeMk).orElse(null);
            if (mataKuliah == null) {
                response.put("success", false);
                response.put("message", "Mata kuliah tidak ditemukan");
                return response;
            }

            MataKuliahDosen mkDosen = mkDosenRepo.findById_IdUserAndKodeMK(user.getIdUser(), kodeMk);
            if (mkDosen == null || !mkDosen.isActive()) {
                response.put("success", false);
                response.put("message", "Anda tidak berhak mengakses mata kuliah ini");
                return response;
            }

            TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
            if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(kodeMk)) {
                response.put("success", false);
                response.put("message", "Tugas tidak ditemukan");
                return response;
            }

            RubrikNilai rubrik = tugas.getRubrik();
            if (rubrik == null) {
                rubrik = new RubrikNilai();
                rubrik = rubrikNilaiRepo.save(rubrik);
                tugas.setRubrik(rubrik);
                tugasRepo.save(tugas);
            }

            if (deletedKomponenIds != null && !deletedKomponenIds.isEmpty()) {
                for (Integer idKomponen : deletedKomponenIds) {
                    KomponenNilai komponen = komponenNilaiRepo.findById(idKomponen).orElse(null);
                    if (komponen != null && komponen.getRubrik().getIdRubrik().equals(rubrik.getIdRubrik())) {
                        komponenNilaiRepo.delete(komponen);
                    }
                }
            }

            if (jadwalList != null && !jadwalList.isEmpty()) {
                for (Map<String, Object> jadwal : jadwalList) {
                    Object idKomponenObj = jadwal.get("idTugas");
                    String namaKomponen = (String) jadwal.get("namaKomponen");
                    String tanggal = (String) jadwal.get("tanggal");
                    String jam = (String) jadwal.get("jam");
                    Boolean isNew = (Boolean) jadwal.get("isNew");

                    LocalDateTime deadline;
                    try {
                        LocalDate date = LocalDate.parse(tanggal);
                        LocalTime time = LocalTime.parse(jam);
                        deadline = LocalDateTime.of(date, time);
                    } catch (Exception e) {
                        response.put("success", false);
                        response.put("message", "Format tanggal atau jam tidak valid");
                        return response;
                    }

                    if (isNew != null && isNew) {
                        
                        KomponenNilai newKomponen = new KomponenNilai();
                        newKomponen.setRubrik(rubrik);
                        newKomponen.setNamaKomponen(namaKomponen);
                        newKomponen.setBobot(0); 
                        newKomponen.setCatatan("");
                        
                        if (tugas.getDeadline() == null || deadline.isAfter(tugas.getDeadline())) {
                            tugas.setDeadline(deadline);
                        }
                        
                        komponenNilaiRepo.save(newKomponen);
                        tugasRepo.save(tugas);
                        
                    } else {
                        
                        Integer idKomponen = ((Number) idKomponenObj).intValue();
                        KomponenNilai komponen = komponenNilaiRepo.findById(idKomponen).orElse(null);
                        
                        if (komponen != null && komponen.getRubrik().getIdRubrik().equals(rubrik.getIdRubrik())) {
                            
                            komponenNilaiRepo.save(komponen);
                            
                            if (tugas.getDeadline() == null || deadline.isAfter(tugas.getDeadline())) {
                                tugas.setDeadline(deadline);
                                tugasRepo.save(tugas);
                            }
                        }
                    }
                }
            }

            response.put("success", true);
            response.put("message", "Jadwal penilaian berhasil disimpan");
            
        } catch (Exception e) {
            System.err.println("Error saving jadwal: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Gagal menyimpan jadwal: " + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/dosen/pemberian-nilai-rubrik")
    public String dosenPemberianNilaiRubrik(
            @RequestParam(required = false) String kodeMk,
            @RequestParam(required = false) Integer idTugas,
            @RequestParam(required = false) Integer idKelompok,
            @RequestParam(required = false) Integer colorIndex,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        
        if (kodeMk == null || kodeMk.isEmpty()) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(kodeMk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliahDosen mkDosen = mkDosenRepo.findById_IdUserAndKodeMK(user.getIdUser(), kodeMk);
        if (mkDosen == null || !mkDosen.isActive()) {
            return "redirect:/dosen/mata-kuliah";
        }

        TugasBesar tugas = null;
        if (idTugas != null) {
            tugas = tugasRepo.findById(idTugas).orElse(null);
            if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(kodeMk)) {
                return "redirect:/dosen/mata-kuliah";
            }
        }

        Kelompok kelompok = null;
        if (idKelompok != null) {
            kelompok = kelompokRepo.findById(idKelompok).orElse(null);
            if (kelompok == null) {
                return "redirect:/dosen/pemberian-nilai?kodeMk=" + kodeMk + "&idTugas=" + idTugas;
            }
        }

        RubrikNilai rubrik = tugas != null ? tugas.getRubrik() : null;
        Set<KomponenNilai> komponenList = rubrik != null ? rubrik.getKomponenList() : new HashSet<>();

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
        
        model.addAttribute("user", user);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("mkDosen", mkDosen);
        model.addAttribute("tugas", tugas);
        model.addAttribute("kelompok", kelompok);
        model.addAttribute("rubrik", rubrik);
        model.addAttribute("komponenList", komponenList);
        model.addAttribute("kodeMk", kodeMk);
        model.addAttribute("idTugas", idTugas);
        model.addAttribute("idKelompok", idKelompok);
        model.addAttribute("colorIndex", finalColorIndex);

        return "nilai/Dosen/pemberian-nilai-rubrik-dosen";
    }

    @GetMapping("/dosen/dashboard-penilaian")
    public String dosenDashboardPenilaian(
            @RequestParam(required = false) String kodeMk,
            @RequestParam(required = false) Integer idTugas,
            @RequestParam(required = false) Integer colorIndex,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        
        if (kodeMk == null || kodeMk.isEmpty() || idTugas == null) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(kodeMk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliahDosen mkDosen = mkDosenRepo.findById_IdUserAndKodeMK(user.getIdUser(), kodeMk);
        if (mkDosen == null || !mkDosen.isActive()) {
            return "redirect:/dosen/mata-kuliah";
        }

        TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
        if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(kodeMk)) {
            return "redirect:/dosen/mata-kuliah";
        }

        List<TugasBesar> tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
        
        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
        
        model.addAttribute("user", user);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("mkDosen", mkDosen);
        model.addAttribute("tugasList", tugasList);
        model.addAttribute("kodeMk", kodeMk);
        model.addAttribute("idTugas", idTugas);
        model.addAttribute("colorIndex", finalColorIndex);

        return "nilai/Dosen/dashboard-nilai-dosen";
    }

    @GetMapping("/dosen/rubrik-penilaian")
    public String dosenRubrikPenilaian(
            @RequestParam(required = false) String kodeMk,
            @RequestParam(required = false) Integer idTugas,
            @RequestParam(required = false) Integer colorIndex,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        
        if (kodeMk == null || kodeMk.isEmpty() || idTugas == null) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(kodeMk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliahDosen mkDosen = mkDosenRepo.findById_IdUserAndKodeMK(user.getIdUser(), kodeMk);
        if (mkDosen == null || !mkDosen.isActive()) {
            return "redirect:/dosen/mata-kuliah";
        }

        TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
        if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(kodeMk)) {
            return "redirect:/dosen/mata-kuliah";
        }

        List<Map<String, Object>> rubrikItems = new ArrayList<>();
        int totalBobot = 0;
        boolean hasRubrik = false;
        
        if (tugas.getRubrik() != null) {
            hasRubrik = true;
            RubrikNilai rubrik = tugas.getRubrik();
            
            List<KomponenNilai> komponenList = komponenNilaiRepo.findByRubrik_IdRubrik(rubrik.getIdRubrik());
            System.out.println("DEBUG: Loaded " + komponenList.size() + " komponens for rubrik " + rubrik.getIdRubrik());
            
            if (komponenList != null && !komponenList.isEmpty()) {
                for (KomponenNilai komponen : komponenList) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("namaKomponen", komponen.getNamaKomponen());
                    item.put("bobot", komponen.getBobot());
                    item.put("catatan", komponen.getCatatan() != null ? komponen.getCatatan() : "");
                    rubrikItems.add(item);
                    totalBobot += komponen.getBobot();
                    System.out.println("DEBUG: Komponen - " + komponen.getNamaKomponen() + ", bobot=" + komponen.getBobot());
                }
            }
        }

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
        
        model.addAttribute("user", user);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("mkDosen", mkDosen);
        model.addAttribute("tugas", tugas);
        model.addAttribute("kodeMk", kodeMk);
        model.addAttribute("idTugas", idTugas);
        model.addAttribute("colorIndex", finalColorIndex);
        model.addAttribute("rubrikItems", rubrikItems);
        model.addAttribute("totalBobot", totalBobot);
        model.addAttribute("hasRubrik", hasRubrik);

        return "nilai/Dosen/rubrik-penilaian-dosen";
    }

    @GetMapping("/dosen/edit-rubrik-penilaian")
    public String dosenEditRubrik(
            @RequestParam(required = false) String kodeMk,
            @RequestParam(required = false) Integer idTugas,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        
        if (kodeMk == null || kodeMk.isEmpty() || idTugas == null) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(kodeMk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/dosen/mata-kuliah";
        }

        MataKuliahDosen mkDosen = mkDosenRepo.findById_IdUserAndKodeMK(user.getIdUser(), kodeMk);
        if (mkDosen == null || !mkDosen.isActive()) {
            return "redirect:/dosen/mata-kuliah";
        }

        TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
        if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(kodeMk)) {
            return "redirect:/dosen/mata-kuliah";
        }

        List<Map<String, Object>> rubrikItems = new ArrayList<>();
        if (tugas.getRubrik() != null) {
            List<KomponenNilai> komponenList = komponenNilaiRepo.findByRubrik_IdRubrik(tugas.getRubrik().getIdRubrik());
            if (komponenList != null && !komponenList.isEmpty()) {
                for (KomponenNilai komponen : komponenList) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("namaKomponen", komponen.getNamaKomponen());
                    item.put("bobot", komponen.getBobot());
                    item.put("catatan", komponen.getCatatan() != null ? komponen.getCatatan() : "");
                    rubrikItems.add(item);
                }
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("mkDosen", mkDosen);
        model.addAttribute("tugas", tugas);
        model.addAttribute("kodeMk", kodeMk);
        model.addAttribute("idTugas", idTugas);
        model.addAttribute("rubrikItems", rubrikItems);

        return "nilai/Dosen/edit-rubrik-dosen";
    }

    @PostMapping("/dosen/rubrik-penilaian/save")
    public String saveRubrik(
            @RequestParam String kodeMk,
            @RequestParam Integer idTugas,
            @RequestParam(required = false) List<String> komponenPenilaian,
            @RequestParam(required = false) List<Integer> bobot,
            @RequestParam(required = false) List<String> keterangan,
            @AuthenticationPrincipal CustomUserDetails user,
            RedirectAttributes redirectAttributes) {
        
        logger.info("=========== SAVE RUBRIK METHOD CALLED ===========");
        logger.info("kodeMk: {}, idTugas: {}", kodeMk, idTugas);
        logger.info("komponenPenilaian: {}", komponenPenilaian);
        logger.info("bobot: {}", bobot);
        logger.info("keterangan: {}", keterangan);
        
        System.out.println("\n\n=== POST /dosen/rubrik-penilaian/save RECEIVED ===");
        System.out.println("kodeMk: " + kodeMk);
        System.out.println("idTugas: " + idTugas);
        System.out.println("komponenPenilaian: " + (komponenPenilaian != null ? komponenPenilaian : "NULL"));
        System.out.println("bobot: " + (bobot != null ? bobot : "NULL"));
        System.out.println("keterangan: " + (keterangan != null ? keterangan : "NULL"));
        System.out.println("komponenPenilaian details: ");
        if (komponenPenilaian != null) {
            for (int i = 0; i < komponenPenilaian.size(); i++) {
                String komp = komponenPenilaian.get(i);
                Integer bob = bobot != null && i < bobot.size() ? bobot.get(i) : null;
                String ket = keterangan != null && i < keterangan.size() ? keterangan.get(i) : null;
                System.out.println("  [" + i + "] nama='" + komp + "', bobot=" + bob + ", ket='" + ket + "'");
            }
        }
        
        try {
            
            System.out.println("DEBUG: ==== saveRubrik START ====");
            System.out.println("DEBUG: kodeMk=" + kodeMk + ", idTugas=" + idTugas);
            System.out.println("DEBUG: komponenPenilaian=" + (komponenPenilaian != null ? komponenPenilaian.size() : "null"));
            System.out.println("DEBUG: bobot=" + (bobot != null ? bobot.size() : "null"));
            System.out.println("DEBUG: keterangan=" + (keterangan != null ? keterangan.size() : "null"));
            
            if (komponenPenilaian != null) {
                for (int i = 0; i < komponenPenilaian.size(); i++) {
                    String namaKompC = komponenPenilaian.get(i);
                    System.out.println("DEBUG: [" + i + "] '" + namaKompC + "' (length=" + (namaKompC != null ? namaKompC.length() : "null") + "), bobot=" + (bobot != null && i < bobot.size() ? bobot.get(i) : "null"));
                }
            }
            
            if (kodeMk == null || kodeMk.isEmpty() || idTugas == null) {
                redirectAttributes.addFlashAttribute("error", "Parameter tidak valid");
                return "redirect:/dosen/mata-kuliah";
            }

            MataKuliah mataKuliah = mataKuliahRepo.findById(kodeMk).orElse(null);
            if (mataKuliah == null) {
                redirectAttributes.addFlashAttribute("error", "Mata kuliah tidak ditemukan");
                return "redirect:/dosen/mata-kuliah";
            }

            MataKuliahDosen mkDosen = mkDosenRepo.findById_IdUserAndKodeMK(user.getIdUser(), kodeMk);
            if (mkDosen == null || !mkDosen.isActive()) {
                redirectAttributes.addFlashAttribute("error", "Anda tidak berhak mengakses mata kuliah ini");
                return "redirect:/dosen/mata-kuliah";
            }

            TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
            if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(kodeMk)) {
                redirectAttributes.addFlashAttribute("error", "Tugas tidak ditemukan");
                return "redirect:/dosen/mata-kuliah";
            }

            int totalBobot = 0;
            if (bobot != null && !bobot.isEmpty()) {
                for (Integer b : bobot) {
                    totalBobot += (b != null ? b : 0);
                }
            }
            
            System.out.println("DEBUG: Total bobot validation = " + totalBobot);

            if (totalBobot != 100) {
                System.err.println("ERROR: Total bobot tidak 100%, actual: " + totalBobot);
                redirectAttributes.addFlashAttribute("error", "Total bobot harus 100%. Saat ini: " + totalBobot + "%");
                return "redirect:/dosen/edit-rubrik-penilaian?kodeMk=" + kodeMk + "&idTugas=" + idTugas;
            }

            RubrikNilai rubrik = tugas.getRubrik();
            
            if (rubrik == null) {
                rubrik = new RubrikNilai();
                rubrik.setKomponenList(new HashSet<>());
                rubrik = rubrikNilaiRepo.save(rubrik);
                System.out.println("DEBUG: New rubrik created with ID = " + rubrik.getIdRubrik());
            } else {
                
                rubrik = rubrikNilaiRepo.findById(rubrik.getIdRubrik()).orElse(rubrik);
                System.out.println("DEBUG: Existing rubrik loaded with ID = " + rubrik.getIdRubrik());
                
                if (rubrik.getKomponenList() != null && !rubrik.getKomponenList().isEmpty()) {
                    int oldSize = rubrik.getKomponenList().size();
                    rubrik.getKomponenList().clear();
                    rubrikNilaiRepo.save(rubrik); 
                    System.out.println("DEBUG: Cleared " + oldSize + " existing components");
                }
                
                if (rubrik.getKomponenList() == null) {
                    rubrik.setKomponenList(new HashSet<>());
                }
            }

            if (komponenPenilaian != null && !komponenPenilaian.isEmpty()) {
                System.out.println("DEBUG: Saving " + komponenPenilaian.size() + " components");
                System.out.println("DEBUG: Rubrik ID = " + rubrik.getIdRubrik());
                System.out.println("DEBUG: Rubrik.getKomponenList() = " + (rubrik.getKomponenList() != null ? "NOT NULL" : "NULL"));
                
                for (int i = 0; i < komponenPenilaian.size(); i++) {
                    String namaKomp = komponenPenilaian.get(i);
                    int bobotVal = bobot != null && i < bobot.size() ? bobot.get(i) : 0;
                    String keteranganVal = keterangan != null && i < keterangan.size() ? keterangan.get(i) : null;
                    
                    System.out.println("DEBUG: ===== Processing Komponen " + i + " =====");
                    System.out.println("DEBUG: namaKomp='" + namaKomp + "' (null=" + (namaKomp == null) + ")");
                    System.out.println("DEBUG: bobotVal=" + bobotVal);
                    System.out.println("DEBUG: keteranganVal='" + keteranganVal + "'");
                    
                    if (namaKomp == null || namaKomp.trim().isEmpty()) {
                        System.err.println("WARNING: Komponen " + i + " has empty name, skipping");
                        continue;
                    }
                    
                    System.out.println("DEBUG: Creating KomponenNilai object");
                    KomponenNilai komponen = new KomponenNilai();
                    komponen.setRubrik(rubrik);
                    komponen.setNamaKomponen(namaKomp.trim());
                    komponen.setBobot(bobotVal);
                    komponen.setCatatan(keteranganVal != null ? keteranganVal.trim() : "");
                    
                    System.out.println("DEBUG: Saving komponen to repository");
                    KomponenNilai saved = komponenNilaiRepo.save(komponen);
                    System.out.println("DEBUG: Komponen saved with ID = " + saved.getIdKomponen());
                    System.out.println("DEBUG: Saved komponen - nama='" + saved.getNamaKomponen() + "', bobot=" + saved.getBobot());
                    
                    rubrik.getKomponenList().add(saved);
                    System.out.println("DEBUG: Added to rubrik list, current size = " + rubrik.getKomponenList().size());
                }
            } else {
                System.err.println("WARNING: komponenPenilaian is null or empty!");
                System.out.println("DEBUG: komponenPenilaian=" + komponenPenilaian);
            }
            
            System.out.println("DEBUG: Final save rubrik with " + (rubrik.getKomponenList() != null ? rubrik.getKomponenList().size() : 0) + " components");
            rubrik = rubrikNilaiRepo.save(rubrik);
            System.out.println("DEBUG: Rubrik saved");
            
            tugas.setRubrik(rubrik);
            tugasRepo.save(tugas);
            System.out.println("DEBUG: Tugas updated with rubrik ID = " + rubrik.getIdRubrik());
            
            System.out.println("DEBUG: ===== VERIFICATION START =====");
            List<KomponenNilai> savedKomponen = komponenNilaiRepo.findByRubrik_IdRubrik(rubrik.getIdRubrik());
            System.out.println("DEBUG: VERIFICATION - Found " + savedKomponen.size() + " components in DB for rubrik " + rubrik.getIdRubrik());
            for (KomponenNilai k : savedKomponen) {
                System.out.println("DEBUG: DB Komponen - nama='" + k.getNamaKomponen() + "', bobot=" + k.getBobot() + ", catatan='" + (k.getCatatan() != null ? k.getCatatan() : "") + "'");
            }
            System.out.println("DEBUG: ===== VERIFICATION END =====");

            redirectAttributes.addFlashAttribute("success", "Rubrik penilaian berhasil disimpan");
            return "redirect:/dosen/rubrik-penilaian?kodeMk=" + kodeMk + "&idTugas=" + idTugas;
            
        } catch (Exception e) {
            System.err.println("ERROR saving rubrik: " + e.getMessage());
            logger.error("Error saving rubrik", e);
            e.printStackTrace();
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Kesalahan tidak diketahui";
            redirectAttributes.addFlashAttribute("error", "Gagal menyimpan rubrik: " + errorMsg);
            return "redirect:/dosen/edit-rubrik-penilaian?kodeMk=" + kodeMk + "&idTugas=" + idTugas;
        }
    }

    @GetMapping("/dosen/debug-mk")
    @ResponseBody
    public Map<String, Object> debugMK(@RequestParam String userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            
            List<MataKuliahDosen> allMK = mkDosenRepo.findById_IdUserAndIsActive(userId, true);
            result.put("userId", userId);
            result.put("totalMKFound", allMK.size());
            result.put("details", allMK.stream()
                .map(mk -> Map.of(
                    "kodeMK", (Object)mk.getMataKuliah().getKodeMK(),
                    "nama", (Object)mk.getMataKuliah().getNama(),
                    "tahunAkademik", (Object)mk.getTahunAkademik(),
                    "isActive", (Object)mk.isActive(),
                    "kelas", (Object)mk.getKelas()
                ))
                .collect(Collectors.toList()));
            result.put("success", true);
        } catch (Exception e) {
            result.put("error", e.toString());
            result.put("message", e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/dosen/rubrik-test")
    @ResponseBody
    public Map<String, Object> rubrikTest(
            @RequestParam Integer idTugas,
            @AuthenticationPrincipal CustomUserDetails user) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
            if (tugas == null) {
                result.put("success", false);
                result.put("message", "Tugas tidak ditemukan");
                return result;
            }
            
            RubrikNilai rubrik = tugas.getRubrik();
            result.put("idTugas", idTugas);
            result.put("hasRubrik", rubrik != null);
            
            if (rubrik != null) {
                result.put("idRubrik", rubrik.getIdRubrik());
                List<KomponenNilai> komponenList = komponenNilaiRepo.findByRubrik_IdRubrik(rubrik.getIdRubrik());
                result.put("komponenCount", komponenList.size());
                result.put("komponenList", komponenList.stream()
                    .map(k -> Map.of(
                        "nama", (Object)k.getNamaKomponen(),
                        "bobot", (Object)k.getBobot(),
                        "catatan", (Object)(k.getCatatan() != null ? k.getCatatan() : "")
                    ))
                    .collect(Collectors.toList()));
            }
            
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/dosen/debug-raw-sql")
    @ResponseBody
    public Map<String, Object> debugRawSQL(@RequestParam String userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            
            String sql = "SELECT md.id_user, md.kode_mk, md.kelas, md.is_active, md.tahun_akademik, mk.nama " +
                         "FROM mata_kuliah_dosen md " +
                         "LEFT JOIN mata_kuliah mk ON md.kode_mk = mk.kode_mk " +
                         "WHERE md.id_user = ? AND md.is_active = 1";
            
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);
            
            result.put("userId", userId);
            result.put("rowCount", rows.size());
            result.put("rows", rows);
            result.put("success", true);
        } catch (Exception e) {
            result.put("error", e.toString());
            result.put("message", e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/dosen/init-dummy-data")
    @ResponseBody
    public Map<String, Object> initDummyData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            
            jdbcTemplate.update(
                "INSERT IGNORE INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                "20250101", "AIF23001", "A", 1, "2025/2026", 1);
            
            jdbcTemplate.update(
                "INSERT IGNORE INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                "20250101", "AIF23002", "A", 1, "2025/2026", 1);
            
            jdbcTemplate.update(
                "INSERT IGNORE INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                "20250102", "AIF23003", "A", 1, "2025/2026", 1);
            
            jdbcTemplate.update(
                "INSERT IGNORE INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                "20250102", "AIF23004", "B", 1, "2025/2026", 1);
            
            jdbcTemplate.update(
                "INSERT IGNORE INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                "20250103", "AIF23005", "A", 1, "2025/2026", 1);
            
            result.put("success", true);
            result.put("message", "Data dummy initialized successfully!");
            
            List<Map<String, Object>> verification = jdbcTemplate.queryForList(
                "SELECT id_user, COUNT(*) as count FROM mata_kuliah_dosen WHERE tahun_akademik = '2025/2026' AND is_active = 1 GROUP BY id_user");
            result.put("verification", verification);
            
        } catch (Exception e) {
            result.put("error", e.toString());
            result.put("message", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @GetMapping("/dosen/pemberian-nilai")
    public String pemberianNilai(@RequestParam(required = false) Integer idTugas,
                                @RequestParam(required = false) Integer idKelompok,
                                @AuthenticationPrincipal CustomUserDetails user,
                                Model model) {
        
        logger.info("Accessing pemberian-nilai: idTugas={}, user={}", idTugas, user.getIdUser());
        
        if (idTugas == null || idTugas <= 0) {
            logger.warn("Invalid idTugas: {}", idTugas);
            return "redirect:/dosen/mata-kuliah";
        }

        TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
        if (tugas == null) {
            logger.warn("Tugas tidak ditemukan: idTugas={}", idTugas);
            return "redirect:/dosen/mata-kuliah";
        }

        if (!tugas.getDosen().getIdUser().equals(user.getIdUser())) {
            logger.warn("User {} bukan dosen pembuat tugas {}. Dosen sebenarnya: {}", 
                user.getIdUser(), idTugas, tugas.getDosen().getIdUser());
            return "redirect:/dosen/mata-kuliah";
        }

        model.addAttribute("user", user);
        model.addAttribute("tugas", tugas);
        model.addAttribute("mkDetail", tugas.getMataKuliah());

        RubrikNilai rubrik = tugas.getRubrik();
        if (rubrik == null) {
            logger.warn("Rubrik tidak ditemukan untuk tugas: {}", idTugas);
            model.addAttribute("error", "Rubrik untuk tugas ini tidak ditemukan");
            return "dosen/pemberian-nilai";
        }

        List<KomponenNilai> komponenList = new ArrayList<>(rubrik.getKomponenList());
        komponenList.sort(Comparator.comparing(KomponenNilai::getIdKomponen));

        model.addAttribute("rubrik", rubrik);
        model.addAttribute("komponenList", komponenList);

        List<TugasBesarKelompok> tugasKelompokList = tugasKelompokRepo.findByIdTugas(idTugas);
        List<Kelompok> kelompokList = tugasKelompokList.stream()
            .map(TugasBesarKelompok::getKelompok)
            .collect(Collectors.toList());

        kelompokList.sort(Comparator.comparing(Kelompok::getNamaKelompok));

        model.addAttribute("kelompokList", kelompokList);

        if ((idKelompok == null || idKelompok <= 0) && !kelompokList.isEmpty()) {
            idKelompok = kelompokList.get(0).getIdKelompok();
        }

        if (idKelompok != null && idKelompok > 0) {
            Kelompok kelompok = kelompokRepo.findById(idKelompok).orElse(null);
            if (kelompok != null) {
                model.addAttribute("kelompokTerpilih", kelompok);

                List<UserKelompok> anggotaList = userKelompokRepo.findByKelompok_IdKelompok(idKelompok);
                anggotaList.sort(Comparator.comparing(uk -> uk.getUser().getNama()));

                model.addAttribute("anggotaList", anggotaList);

                Map<String, Nilai> nilaiMap = new HashMap<>();
                for (UserKelompok anggota : anggotaList) {
                    Nilai nilai = nilaiRepository
                        .findByUser_IdUserAndTugas_IdTugas(anggota.getUser().getIdUser(), idTugas)
                        .orElse(null);
                    if (nilai != null) {
                        nilaiMap.put(anggota.getUser().getIdUser(), nilai);
                    }
                }
                model.addAttribute("nilaiMap", nilaiMap);
            }
        }

        return "dosen/pemberian-nilai";
    }

    @GetMapping("/dosen/pemberian-nilai-individu")
    public String pemberianNilaiIndividu(@RequestParam(required = false) Integer idTugas,
                                         @AuthenticationPrincipal CustomUserDetails user,
                                         Model model) {
        
        logger.info("Accessing pemberian-nilai-individu: idTugas={}, user={}", idTugas, user.getIdUser());
        
        if (idTugas == null || idTugas <= 0) {
            logger.warn("Invalid idTugas: {}", idTugas);
            return "redirect:/dosen/mata-kuliah";
        }

        TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
        if (tugas == null) {
            logger.warn("Tugas tidak ditemukan: idTugas={}", idTugas);
            return "redirect:/dosen/mata-kuliah";
        }

        if (!tugas.getDosen().getIdUser().equals(user.getIdUser())) {
            logger.warn("User {} bukan dosen pembuat tugas {}. Dosen sebenarnya: {}", 
                user.getIdUser(), idTugas, tugas.getDosen().getIdUser());
            return "redirect:/dosen/mata-kuliah";
        }

        model.addAttribute("user", user);
        model.addAttribute("tugas", tugas);
        model.addAttribute("mkDetail", tugas.getMataKuliah());

        List<TugasBesarKelompok> tugasKelompokList = tugasKelompokRepo.findByIdTugas(idTugas);
        List<Kelompok> kelompokList = tugasKelompokList.stream()
            .map(TugasBesarKelompok::getKelompok)
            .collect(Collectors.toList());

        kelompokList.sort(Comparator.comparing(Kelompok::getNamaKelompok));

        model.addAttribute("kelompokList", kelompokList);

        Map<Integer, List<UserKelompok>> anggotaByKelompok = new HashMap<>();
        Map<String, Nilai> nilaiMap = new HashMap<>();

        for (Kelompok kelompok : kelompokList) {
            List<UserKelompok> anggotaList = userKelompokRepo.findByKelompok_IdKelompok(kelompok.getIdKelompok());
            anggotaList.sort(Comparator.comparing(uk -> uk.getUser().getNama()));
            anggotaByKelompok.put(kelompok.getIdKelompok(), anggotaList);

            for (UserKelompok anggota : anggotaList) {
                Nilai nilai = nilaiRepository
                    .findByUser_IdUserAndTugas_IdTugas(anggota.getUser().getIdUser(), idTugas)
                    .orElse(null);
                if (nilai != null) {
                    nilaiMap.put(anggota.getUser().getIdUser(), nilai);
                }
            }
        }

        model.addAttribute("anggotaByKelompok", anggotaByKelompok);
        model.addAttribute("nilaiMap", nilaiMap);

        try {
            
            StringBuilder kelompokJson = new StringBuilder("[");
            for (int i = 0; i < kelompokList.size(); i++) {
                Kelompok k = kelompokList.get(i);
                if (i > 0) kelompokJson.append(",");
                kelompokJson.append("{\"idKelompok\":").append(k.getIdKelompok())
                    .append(",\"namaKelompok\":\"").append(k.getNamaKelompok()).append("\"}");
            }
            kelompokJson.append("]");
            
            StringBuilder anggotaJson = new StringBuilder("{");
            boolean firstKey = true;
            for (Kelompok kelompok : kelompokList) {
                if (!firstKey) anggotaJson.append(",");
                anggotaJson.append("\"").append(kelompok.getIdKelompok()).append("\":[");
                
                List<UserKelompok> anggotaList = anggotaByKelompok.getOrDefault(kelompok.getIdKelompok(), new ArrayList<>());
                for (int i = 0; i < anggotaList.size(); i++) {
                    UserKelompok uk = anggotaList.get(i);
                    if (i > 0) anggotaJson.append(",");
                    anggotaJson.append("{\"user\":{\"idUser\":\"").append(uk.getUser().getIdUser())
                        .append("\",\"nama\":\"").append(uk.getUser().getNama())
                        .append("\"},\"role\":\"").append(uk.getRole()).append("\"}");
                }
                
                anggotaJson.append("]");
                firstKey = false;
            }
            anggotaJson.append("}");
            
            StringBuilder nilaiJson = new StringBuilder("{");
            boolean firstNilai = true;
            for (String userId : nilaiMap.keySet()) {
                if (!firstNilai) nilaiJson.append(",");
                Nilai n = nilaiMap.get(userId);
                nilaiJson.append("\"").append(userId).append("\":{\"nilaiPribadi\":").append(n.getNilaiPribadi()).append("}");
                firstNilai = false;
            }
            nilaiJson.append("}");
            
            model.addAttribute("kelompokListJson", kelompokJson.toString());
            model.addAttribute("anggotaByKelompokJson", anggotaJson.toString());
            model.addAttribute("nilaiMapJson", nilaiJson.toString());
            
            logger.info("JSON strings built successfully");
        } catch (Exception e) {
            logger.error("Error building JSON strings", e);
            model.addAttribute("kelompokListJson", "[]");
            model.addAttribute("anggotaByKelompokJson", "{}");
            model.addAttribute("nilaiMapJson", "{}");
        }

        return "dosen/pemberian-nilai-individu";
    }

    @PostMapping("/api/nilai/simpan")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> simpanNilai(@RequestBody PemberianNilaiPerKomponenDTO request,
                                         @AuthenticationPrincipal CustomUserDetails user) {
        try {
            
            if (request.getIdUser() == null || request.getIdUser().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID User tidak valid"));
            }
            if (request.getIdTugas() == null || request.getIdTugas() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID Tugas tidak valid"));
            }
            if (request.getNilaiPerKomponen() == null || request.getNilaiPerKomponen().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nilai komponen tidak ada"));
            }

            TugasBesar tugas = tugasRepo.findById(request.getIdTugas())
                .orElseThrow(() -> new IllegalArgumentException("Tugas tidak ditemukan"));

            if (!tugas.getDosen().getIdUser().equals(user.getIdUser())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Anda tidak berhak memberikan nilai untuk tugas ini"));
            }

            if (!nilaiService.isSemuaKomponenTerisi(request.getIdTugas(), request.getNilaiPerKomponen())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Semua komponen harus memiliki nilai"));
            }

            Nilai nilaiTersimpan = nilaiService.simpanNilai(
                request.getIdUser(),
                request.getIdTugas(),
                request.getNilaiPerKomponen(),
                request.isSamaBuat()
            );

            if (request.isSamaBuat()) {
                
                List<UserKelompok> userDalamTugas = userKelompokRepo
                    .findByUser_IdUserAndKelompok_InTugaBesar(request.getIdUser(), request.getIdTugas());
                
                if (!userDalamTugas.isEmpty()) {
                    
                    Integer idKelompok = userDalamTugas.get(0).getKelompok().getIdKelompok();
                    
                    List<UserKelompok> semuaAnggota = userKelompokRepo
                        .findByKelompok_IdKelompok(idKelompok);
                    
                    for (UserKelompok anggota : semuaAnggota) {
                        if (!anggota.getUser().getIdUser().equals(request.getIdUser())) {
                            nilaiService.simpanNilai(
                                anggota.getUser().getIdUser(),
                                request.getIdTugas(),
                                request.getNilaiPerKomponen(),
                                false
                            );
                        }
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Nilai berhasil disimpan");
            response.put("idNilai", nilaiTersimpan.getIdNilai());
            response.put("nilaiKelompok", nilaiTersimpan.getNilaiKelompok());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error menyimpan nilai", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @PostMapping("/api/nilai/simpan-individu")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> simpanNilaiIndividu(@RequestBody Map<String, Object> request,
                                                  @AuthenticationPrincipal CustomUserDetails user) {
        try {
            String idUser = (String) request.get("idUser");
            Integer idTugas = ((Number) request.get("idTugas")).intValue();
            Integer nilaiPribadi = ((Number) request.get("nilaiPribadi")).intValue();

            if (idUser == null || idUser.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID User tidak valid"));
            }
            if (idTugas == null || idTugas <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID Tugas tidak valid"));
            }
            if (nilaiPribadi < 0 || nilaiPribadi > 100) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nilai harus antara 0-100"));
            }

            TugasBesar tugas = tugasRepo.findById(idTugas)
                .orElseThrow(() -> new IllegalArgumentException("Tugas tidak ditemukan"));

            if (!tugas.getDosen().getIdUser().equals(user.getIdUser())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Anda tidak berhak memberikan nilai untuk tugas ini"));
            }

            Nilai nilai = nilaiRepository
                .findByUser_IdUserAndTugas_IdTugas(idUser, idTugas)
                .orElse(null);

            if (nilai == null) {
                
                nilai = new Nilai();
                nilai.setUser(userRepo.findById(idUser)
                    .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan")));
                nilai.setTugas(tugas);
                nilai.setNilaiPribadi(nilaiPribadi);
                nilai.setNilaiKelompok(0); 
            } else {
                
                nilai.setNilaiPribadi(nilaiPribadi);
            }

            Nilai nilaiTersimpan = nilaiRepository.save(nilai);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Nilai individu berhasil disimpan");
            response.put("idNilai", nilaiTersimpan.getIdNilai());
            response.put("nilaiPribadi", nilaiTersimpan.getNilaiPribadi());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error menyimpan nilai individu", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @GetMapping("/api/nilai/{idUser}/{idTugas}")
    @ResponseBody
    public ResponseEntity<?> getNilai(@PathVariable String idUser,
                                      @PathVariable Integer idTugas,
                                      @AuthenticationPrincipal CustomUserDetails user) {
        try {
            
            TugasBesar tugas = tugasRepo.findById(idTugas)
                .orElseThrow(() -> new IllegalArgumentException("Tugas tidak ditemukan"));

            if (!tugas.getDosen().getIdUser().equals(user.getIdUser())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Anda tidak berhak mengakses nilai ini"));
            }

            Nilai nilai = nilaiService.getNilaiByUserAndTugas(idUser, idTugas);

            if (nilai == null) {
                return ResponseEntity.ok(Map.of("exists", false));
            }

            List<NilaiKomponen> nilaiKomponenList = nilaiKomponenRepository.findByNilai_IdNilai(nilai.getIdNilai());

            Map<String, Object> response = new HashMap<>();
            response.put("exists", true);
            response.put("idNilai", nilai.getIdNilai());
            response.put("nilaiKelompok", nilai.getNilaiKelompok());
            response.put("nilaiPribadi", nilai.getNilaiPribadi());

            Map<Integer, Integer> nilaiPerKomponen = new HashMap<>();
            for (NilaiKomponen nk : nilaiKomponenList) {
                nilaiPerKomponen.put(nk.getKomponen().getIdKomponen(), nk.getNilaiKomponen());
            }
            response.put("nilaiPerKomponen", nilaiPerKomponen);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error mengambil nilai", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }

    @PostMapping("/api/nilai/hapus")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> hapusNilai(@RequestParam String idUser,
                                       @RequestParam Integer idTugas,
                                       @AuthenticationPrincipal CustomUserDetails user) {
        try {
            
            TugasBesar tugas = tugasRepo.findById(idTugas)
                .orElseThrow(() -> new IllegalArgumentException("Tugas tidak ditemukan"));

            if (!tugas.getDosen().getIdUser().equals(user.getIdUser())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Anda tidak berhak menghapus nilai ini"));
            }

            nilaiService.hapusNilai(idUser, idTugas);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Nilai berhasil dihapus"
            ));

        } catch (Exception e) {
            logger.error("Error menghapus nilai", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage()));
        }
    }
}
