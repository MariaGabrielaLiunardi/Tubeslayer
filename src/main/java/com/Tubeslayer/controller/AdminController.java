package com.Tubeslayer.controller;

import com.Tubeslayer.dto.MKArchiveDTO;
import com.Tubeslayer.dto.PesertaMatkulDTO;
import com.Tubeslayer.entity.Kelompok;
import com.Tubeslayer.entity.MataKuliah;
import com.Tubeslayer.entity.MataKuliahDosen;
import com.Tubeslayer.entity.MataKuliahMahasiswa;
import com.Tubeslayer.entity.TugasBesar;
import com.Tubeslayer.entity.User;
import com.Tubeslayer.repository.KelompokRepository;
import com.Tubeslayer.repository.MataKuliahDosenRepository;
import com.Tubeslayer.repository.MataKuliahMahasiswaRepository;
import com.Tubeslayer.repository.MataKuliahRepository;
import com.Tubeslayer.repository.TugasBesarRepository;
import com.Tubeslayer.repository.UserRepository;
import com.Tubeslayer.service.AuthService;
import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardAdminService;
import com.Tubeslayer.service.MataKuliahService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DashboardAdminService dashboardService;
    private final MataKuliahService mataKuliahService;
    private final AuthService authService;
    private final MataKuliahDosenRepository mataKuliahDosenRepo;
    private final TugasBesarRepository tugasRepo;
    private final UserRepository userRepository;
    private final MataKuliahRepository mataKuliahRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MataKuliahDosenRepository mkDosenRepo;

    @Autowired
    private MataKuliahMahasiswaRepository mkMahasiswaRepo;

    @Autowired
    private KelompokRepository kelompokRepository;

    public AdminController(
            DashboardAdminService dashboardService,
            MataKuliahService mataKuliahService,
            AuthService authService,
            MataKuliahDosenRepository mataKuliahDosenRepo,
            TugasBesarRepository tugasRepo,
            UserRepository userRepository,
            MataKuliahRepository mataKuliahRepository) {
        this.dashboardService = dashboardService;
        this.mataKuliahService = mataKuliahService;
        this.authService = authService;
        this.mataKuliahDosenRepo = mataKuliahDosenRepo;
        this.tugasRepo = tugasRepo;
        this.userRepository = userRepository;
        this.mataKuliahRepository = mataKuliahRepository;
    }

    @ModelAttribute("user")
    public User addLoggedUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUser() : null;
    }

    // ============================
    // DASHBOARD ADMIN
    // ============================
    @GetMapping("/dashboard")
    public String adminDashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);

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

    // ============================
    // MENU AWAL ADMIN
    // ============================
    @GetMapping("/menu-awal-ad")
    public String menuAwalAdmin(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/menu-awal-ad";
    }

    @GetMapping("/kelola-mata-kuliah")
    public String kelolaMataKuliah(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/kelola-mata-kuliah";
    }

    // ============================
    // KELOLA DOSEN - VIEW PAGES
    // ============================
    @GetMapping("/kelola-dosen")
    @PreAuthorize("hasRole('ADMIN')")
    public String kelolaDosen(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);

        // Ambil data dosen aktif untuk ditampilkan di view
        List<User> dosenList = userRepository.findByRoleAndIsActiveTrue("Dosen");
        model.addAttribute("dosenList", dosenList);
        model.addAttribute("dosenCount", dosenList.size());

        return "admin/kelola-dosen";
    }

    @GetMapping("/kelola-mahasiswa")
    public String kelolaMahasiswa(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        // Ambil data mahasiswa aktif untuk ditampilkan di view
        List<User> mahasiswaList = userRepository.findByRoleAndIsActiveTrue("Mahasiswa");
        model.addAttribute("mahasiswaList", mahasiswaList);
        model.addAttribute("mahasiswaCount", mahasiswaList.size());
        return "admin/kelola-mahasiswa";
    }

    // ============================
    // VIEWS UNTUK ARSIP
    // ============================
    @GetMapping("/arsip-mata-kuliah")
    public String getArsip(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MKArchiveDTO> pageMK = mataKuliahRepository.getArchiveMK(pageable);

        model.addAttribute("arsipMK", pageMK.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageMK.getTotalPages());
        model.addAttribute("totalItems", pageMK.getTotalElements());

        return "admin/arsip-mata-kuliah";
    }

    @GetMapping("/arsip-matkul-detail")
    public String kelolaArsipMatkulDetail(@RequestParam(required = false) String kodeMk,
            @AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);

        if (kodeMk == null || kodeMk.isEmpty()) {
            return "redirect:/admin/dashboard";
        }

        MataKuliah mkDetail = mataKuliahRepository.findById(kodeMk).orElse(null);
        if (mkDetail == null) {
            return "redirect:/admin/dashboard";
        }

        int hashCode = kodeMk.hashCode();
        int randomColorIndex = (Math.abs(hashCode) % 4);

        model.addAttribute("colorIndex", randomColorIndex);

       MataKuliahDosen koordinator = null;
       try {
            List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, false);
            if (!dosenList.isEmpty()) {
                koordinator = dosenList.get(0);
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinator: " + e.getMessage());
        }
        model.addAttribute("koordinator", koordinator);

        List<TugasBesar> tugasList = Collections.emptyList();
        try {
            tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, false); 
        } catch (Exception e) {
            System.err.println("Error fetching tasks for archive: " + e.getMessage());
        }

        List<Map<String, Object>> tugasData = tugasList.stream().map(tugas -> {
            Map<String, Object> map = new HashMap<>();
            map.put("judulTugas", tugas.getJudulTugas());
            map.put("idTugas", tugas.getIdTugas());
            map.put("kelompokCount", 0);
            map.put("submissionCount", 0);

            return map;
        }).collect(Collectors.toList());
    
        model.addAttribute("mkDetail", mkDetail);
        model.addAttribute("tugasDataList", tugasData);

        return "admin/arsip-matkul-detail";
    }

    @GetMapping("/matkul-peserta")
    public String kelolaArsipMatkulPeserta(
            @RequestParam(required = false) String kodeMk,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {

        model.addAttribute("user", user);

        if (kodeMk == null || kodeMk.isEmpty()) {
            return "redirect:/admin/dashboard";
        }

        MataKuliah mk = mataKuliahRepository.findById(kodeMk).orElse(null);
        if (mk == null) {
            return "redirect:/admin/dashboard";
        }

        int hashCode = kodeMk.hashCode();
        int randomColorIndex = (Math.abs(hashCode) % 4);

        model.addAttribute("colorIndex", randomColorIndex);

        MataKuliahDosen koordinator = null;
        try {
            List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, false); 
            if (!dosenList.isEmpty()) {
                koordinator = dosenList.get(0);
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinator for archive: " + e.getMessage());
        }
        model.addAttribute("koordinator", koordinator);

        List<MataKuliahMahasiswa> listPeserta = Collections.emptyList();

        if (mkMahasiswaRepo != null && mk != null) {
            try {
                listPeserta = mkMahasiswaRepo.findByMataKuliah_KodeMKAndIsActive(mk.getKodeMK(), false);
            } catch (Exception e) {
                System.err.println("Error saat mengambil data peserta arsip: " + e.getMessage());
            }
        }

        model.addAttribute("mkDetail", mk);
        model.addAttribute("pesertaList", listPeserta);

        return "admin/matkul-peserta";
    }

    // ============================
    // API ENDPOINTS - KELOLA DOSEN (AKTIF SAJA)
    // ============================

    /**
     * API: Ambil semua dosen AKTIF (JSON)
     */
    @GetMapping("/api/dosen")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllDosenApi(
            @RequestParam(required = false, defaultValue = "aktif") String status) {
        try {
            List<User> dosenList;
            
            if ("semua".equalsIgnoreCase(status)) {
                dosenList = userRepository.findByRole("Dosen");
            } else if ("nonaktif".equalsIgnoreCase(status)) {
                // Mengambil dosen nonaktif dengan custom query
                dosenList = userRepository.findByRole("Dosen").stream()
                        .filter(dosen -> !dosen.isActive())
                        .collect(Collectors.toList());
            } else {
                // Default: ambil hanya yang aktif
                dosenList = userRepository.findByRoleAndIsActiveTrue("Dosen");
            }

            List<Map<String, Object>> formattedList = dosenList.stream()
                    .map(dosen -> {
                        Map<String, Object> dosenMap = new HashMap<>();
                        dosenMap.put("id", dosen.getIdUser());
                        dosenMap.put("nip", extractNipFromId(dosen.getIdUser()));
                        dosenMap.put("nama", dosen.getNama());
                        dosenMap.put("email", dosen.getEmail());
                        dosenMap.put("role", dosen.getRole());
                        dosenMap.put("status", dosen.isActive() ? "Aktif" : "Nonaktif");
                        dosenMap.put("isActive", dosen.isActive());
                        return dosenMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", formattedList);
            response.put("count", formattedList.size());
            response.put("filter", status);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mengambil data dosen: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Cari dosen AKTIF berdasarkan nama atau NIP
     */
    @GetMapping("/api/dosen/search")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> searchDosenApi(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "aktif") String status) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllDosenApi(status);
            }

            String searchKeyword = keyword.trim().toLowerCase();
            
            // Ambil data berdasarkan filter status
            List<User> baseList;
            if ("semua".equalsIgnoreCase(status)) {
                baseList = userRepository.findByRole("Dosen");
            } else if ("nonaktif".equalsIgnoreCase(status)) {
                baseList = userRepository.findByRole("Dosen").stream()
                        .filter(dosen -> !dosen.isActive())
                        .collect(Collectors.toList());
            } else {
                baseList = userRepository.findByRoleAndIsActiveTrue("Dosen");
            }

            // Filter berdasarkan keyword
            List<User> results = baseList.stream()
                    .filter(dosen -> 
                        dosen.getNama().toLowerCase().contains(searchKeyword) ||
                        dosen.getIdUser().toLowerCase().contains(searchKeyword) ||
                        dosen.getEmail().toLowerCase().contains(searchKeyword))
                    .collect(Collectors.toList());

            List<Map<String, Object>> formattedResults = results.stream()
                    .map(dosen -> {
                        Map<String, Object> dosenMap = new HashMap<>();
                        dosenMap.put("id", dosen.getIdUser());
                        dosenMap.put("nip", extractNipFromId(dosen.getIdUser()));
                        dosenMap.put("nama", dosen.getNama());
                        dosenMap.put("email", dosen.getEmail());
                        dosenMap.put("status", dosen.isActive() ? "Aktif" : "Nonaktif");
                        dosenMap.put("isActive", dosen.isActive());
                        return dosenMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", formattedResults);
            response.put("count", formattedResults.size());
            response.put("filter", status);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mencari dosen: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Tambah dosen baru (default aktif)
     */
    @PostMapping("/api/dosen")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> addDosenApi(@RequestBody Map<String, String> dosenData) {
        try {
            String nama = dosenData.get("nama");
            if (nama == null || nama.trim().isEmpty()) {
                return buildErrorResponse("Nama dosen tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }

            nama = nama.trim();

            String nip = dosenData.getOrDefault("nip", "");
            if (nip.isEmpty()) {
                nip = generateDosenNip();
            }

            if (userRepository.existsById(nip)) {
                return buildErrorResponse("NIP " + nip + " sudah terdaftar", HttpStatus.BAD_REQUEST);
            }

            User dosen = new User();
            dosen.setIdUser(nip);
            dosen.setNama(nama);

            String email = dosenData.getOrDefault("email", "");
            if (email.isEmpty()) {
                email = generateEmail(nama, nip);
            }
            dosen.setEmail(email);

            String defaultPassword = "$2a$10$lpXunJk2Te8/hHcfFFmpduViPATPUYuau.rAK1ckJbpDh5m8MSXV2";
            dosen.setPassword(defaultPassword);
            dosen.setRole("Dosen");

            // Default status adalah aktif
            String status = dosenData.getOrDefault("status", "aktif");
            dosen.setActive("aktif".equalsIgnoreCase(status) || "1".equals(status) || "true".equalsIgnoreCase(status));

            User savedDosen = userRepository.save(dosen);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dosen " + nama + " berhasil ditambahkan");
            response.put("data", mapDosenToResponse(savedDosen));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal menambahkan dosen: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Hapus dosen (soft delete - nonaktifkan)
     */
    @DeleteMapping("/api/dosen/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteDosenApi(@PathVariable String id) {
        try {
            Optional<User> dosenOpt = userRepository.findById(id);

            if (dosenOpt.isEmpty()) {
                return buildErrorResponse("Dosen dengan ID " + id + " tidak ditemukan",
                        HttpStatus.NOT_FOUND);
            }

            User dosen = dosenOpt.get();

            if (!"Dosen".equals(dosen.getRole())) {
                return buildErrorResponse("Hanya dapat menghapus user dengan role Dosen",
                        HttpStatus.BAD_REQUEST);
            }

            // Soft delete: nonaktifkan dosen
            dosen.setActive(false);
            userRepository.save(dosen);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dosen " + dosen.getNama() + " berhasil dinonaktifkan");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal menghapus dosen: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Update status dosen (aktif/nonaktif)
     */
    @PutMapping("/api/dosen/{id}/status")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateDosenStatusApi(
            @PathVariable String id,
            @RequestBody Map<String, String> statusData) {
        try {
            Optional<User> dosenOpt = userRepository.findById(id);

            if (dosenOpt.isEmpty()) {
                return buildErrorResponse("Dosen tidak ditemukan", HttpStatus.NOT_FOUND);
            }

            User dosen = dosenOpt.get();

            if (!"Dosen".equals(dosen.getRole())) {
                return buildErrorResponse("Hanya dapat mengubah status user dengan role Dosen",
                        HttpStatus.BAD_REQUEST);
            }

            String status = statusData.get("status");
            if (status == null) {
                return buildErrorResponse("Status tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }

            boolean newStatus = "aktif".equalsIgnoreCase(status) || "1".equals(status) || "true".equalsIgnoreCase(status);
            dosen.setActive(newStatus);

            userRepository.save(dosen);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Status dosen berhasil diperbarui menjadi " +
                    (newStatus ? "Aktif" : "Nonaktif"));
            response.put("data", mapDosenToResponse(dosen));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal memperbarui status dosen: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Update data dosen
     */
    @PutMapping("/api/dosen/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateDosenApi(
            @PathVariable String id,
            @RequestBody Map<String, String> dosenData) {
        try {
            Optional<User> dosenOpt = userRepository.findById(id);

            if (dosenOpt.isEmpty()) {
                return buildErrorResponse("Dosen tidak ditemukan", HttpStatus.NOT_FOUND);
            }

            User dosen = dosenOpt.get();

            if (!"Dosen".equals(dosen.getRole())) {
                return buildErrorResponse("Hanya dapat mengubah user dengan role Dosen",
                        HttpStatus.BAD_REQUEST);
            }

            if (dosenData.containsKey("nama") && !dosenData.get("nama").trim().isEmpty()) {
                dosen.setNama(dosenData.get("nama").trim());
            }

            if (dosenData.containsKey("email") && !dosenData.get("email").trim().isEmpty()) {
                dosen.setEmail(dosenData.get("email").trim());
            }

            if (dosenData.containsKey("status")) {
                String status = dosenData.get("status");
                dosen.setActive("aktif".equalsIgnoreCase(status) || "1".equals(status) || "true".equalsIgnoreCase(status));
            }

            userRepository.save(dosen);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Data dosen berhasil diperbarui");
            response.put("data", mapDosenToResponse(dosen));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal memperbarui data dosen: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Import dosen dari file Excel/CSV (berfungsi lengkap)
     */
    @PostMapping("/api/dosen/import")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importDosenApi(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return buildErrorResponse("File tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }

            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();

            if (!isValidFileType(contentType, fileName)) {
                return buildErrorResponse(
                        "Format file tidak didukung. Gunakan file CSV (.csv) atau Excel (.xlsx, .xls)",
                        HttpStatus.BAD_REQUEST);
            }

            List<User> importedDosen = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            List<String> successes = new ArrayList<>();

            if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                // Parse file Excel
                parseExcelFile(file, importedDosen, errors, successes, "Dosen");
            } else if (fileName.endsWith(".csv")) {
                // Parse file CSV (akan kita implementasikan nanti)
                parseCSVFile(file, importedDosen, errors, successes, "Dosen");
            }

            // Simpan dosen yang berhasil diimport
            List<Map<String, Object>> savedDosen = new ArrayList<>();
            for (User dosen : importedDosen) {
                try {
                    User saved = userRepository.save(dosen);
                    savedDosen.add(mapDosenToResponse(saved));
                    successes.add("Dosen " + dosen.getNama() + " berhasil diimport");
                } catch (Exception e) {
                    errors.add("Gagal menyimpan dosen " + dosen.getNama() + ": " + e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Import file " + fileName + " selesai");
            response.put("filename", fileName);
            response.put("totalImported", importedDosen.size());
            response.put("successCount", successes.size());
            response.put("errorCount", errors.size());
            response.put("successMessages", successes);
            response.put("errorMessages", errors);
            response.put("importedData", savedDosen);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mengimpor file: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Download template Excel untuk import dosen
     */
    @GetMapping("/api/dosen/template")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadDosenTemplate() throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Template Dosen");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("NIP");
            headerRow.createCell(1).setCellValue("Nama");
            headerRow.createCell(2).setCellValue("Email (opsional)");
            
            // Add example data
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("123456");
            exampleRow.createCell(1).setCellValue("Dr. John Doe");
            exampleRow.createCell(2).setCellValue("john.doe@unpar.ac.id");
            
            // Auto-size columns
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=template_import_dosen.xlsx")
                    .body(outputStream.toByteArray());
                    
        } catch (Exception e) {
            throw new Exception("Gagal membuat template: " + e.getMessage());
        }
    }
    
   

    /**
     * API: Get dosen by ID (untuk edit form)
     */
    @GetMapping("/api/dosen/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDosenByIdApi(@PathVariable String id) {
        try {
            Optional<User> dosenOpt = userRepository.findById(id);

            if (dosenOpt.isEmpty()) {
                return buildErrorResponse("Dosen tidak ditemukan", HttpStatus.NOT_FOUND);
            }

            User dosen = dosenOpt.get();

            if (!"Dosen".equals(dosen.getRole())) {
                return buildErrorResponse("User bukan dosen", HttpStatus.BAD_REQUEST);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", mapDosenToResponse(dosen));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mengambil data dosen: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ============================
    // API ENDPOINTS - KELOLA MAHASISWA (AKTIF SAJA)
    // ============================

    /**
     * API: Ambil semua mahasiswa AKTIF (JSON)
     */
    @GetMapping("/api/mahasiswa")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllMahasiswaApi(
            @RequestParam(required = false, defaultValue = "aktif") String status) {
        try {
            List<User> mahasiswaList;
            
            if ("semua".equalsIgnoreCase(status)) {
                mahasiswaList = userRepository.findByRole("Mahasiswa");
            } else if ("nonaktif".equalsIgnoreCase(status)) {
                // Mengambil mahasiswa nonaktif dengan custom query
                mahasiswaList = userRepository.findByRole("Mahasiswa").stream()
                        .filter(mahasiswa -> !mahasiswa.isActive())
                        .collect(Collectors.toList());
            } else {
                // Default: ambil hanya yang aktif
                mahasiswaList = userRepository.findByRoleAndIsActiveTrue("Mahasiswa");
            }

            List<Map<String, Object>> formattedList = mahasiswaList.stream()
                    .map(mahasiswa -> {
                        Map<String, Object> mahasiswaMap = new HashMap<>();
                        mahasiswaMap.put("id", mahasiswa.getIdUser());
                        mahasiswaMap.put("npm", extractNpmFromId(mahasiswa.getIdUser()));
                        mahasiswaMap.put("nama", mahasiswa.getNama());
                        mahasiswaMap.put("email", mahasiswa.getEmail());
                        mahasiswaMap.put("role", mahasiswa.getRole());
                        mahasiswaMap.put("status", mahasiswa.isActive() ? "Aktif" : "Nonaktif");
                        mahasiswaMap.put("isActive", mahasiswa.isActive());
                        return mahasiswaMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", formattedList);
            response.put("count", formattedList.size());
            response.put("filter", status);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mengambil data mahasiswa: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Cari mahasiswa AKTIF berdasarkan nama atau NPM
     */
    @GetMapping("/api/mahasiswa/search")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> searchMahasiswaApi(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "aktif") String status) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllMahasiswaApi(status);
            }

            String searchKeyword = keyword.trim().toLowerCase();
            
            // Ambil data berdasarkan filter status
            List<User> baseList;
            if ("semua".equalsIgnoreCase(status)) {
                baseList = userRepository.findByRole("Mahasiswa");
            } else if ("nonaktif".equalsIgnoreCase(status)) {
                baseList = userRepository.findByRole("Mahasiswa").stream()
                        .filter(mahasiswa -> !mahasiswa.isActive())
                        .collect(Collectors.toList());
            } else {
                baseList = userRepository.findByRoleAndIsActiveTrue("Mahasiswa");
            }

            // Filter berdasarkan keyword
            List<User> results = baseList.stream()
                    .filter(mahasiswa -> 
                        mahasiswa.getNama().toLowerCase().contains(searchKeyword) ||
                        mahasiswa.getIdUser().toLowerCase().contains(searchKeyword) ||
                        mahasiswa.getEmail().toLowerCase().contains(searchKeyword))
                    .collect(Collectors.toList());

            List<Map<String, Object>> formattedResults = results.stream()
                    .map(mahasiswa -> {
                        Map<String, Object> mahasiswaMap = new HashMap<>();
                        mahasiswaMap.put("id", mahasiswa.getIdUser());
                        mahasiswaMap.put("npm", extractNpmFromId(mahasiswa.getIdUser()));
                        mahasiswaMap.put("nama", mahasiswa.getNama());
                        mahasiswaMap.put("email", mahasiswa.getEmail());
                        mahasiswaMap.put("status", mahasiswa.isActive() ? "Aktif" : "Nonaktif");
                        mahasiswaMap.put("isActive", mahasiswa.isActive());
                        return mahasiswaMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", formattedResults);
            response.put("count", formattedResults.size());
            response.put("filter", status);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mencari mahasiswa: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Tambah mahasiswa baru (default aktif)
     */
    @PostMapping("/api/mahasiswa")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> addMahasiswaApi(@RequestBody Map<String, String> mahasiswaData) {
        try {
            String nama = mahasiswaData.get("nama");
            if (nama == null || nama.trim().isEmpty()) {
                return buildErrorResponse("Nama mahasiswa tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }

            nama = nama.trim();

            String npm = mahasiswaData.getOrDefault("npm", "");
            if (npm.isEmpty()) {
                npm = generateMahasiswaNpm();
            }

            if (userRepository.existsById(npm)) {
                return buildErrorResponse("NPM " + npm + " sudah terdaftar", HttpStatus.BAD_REQUEST);
            }

            User mahasiswa = new User();
            mahasiswa.setIdUser(npm);
            mahasiswa.setNama(nama);

            String email = mahasiswaData.getOrDefault("email", "");
            if (email.isEmpty()) {
                email = generateEmailMahasiswa(nama, npm);
            }
            mahasiswa.setEmail(email);

            String defaultPassword = "$2a$10$lpXunJk2Te8/hHcfFFmpduViPATPUYuau.rAK1ckJbpDh5m8MSXV2";
            mahasiswa.setPassword(defaultPassword);
            mahasiswa.setRole("Mahasiswa");

            // Default status adalah aktif
            String status = mahasiswaData.getOrDefault("status", "aktif");
            mahasiswa.setActive("aktif".equalsIgnoreCase(status) || "1".equals(status) || "true".equalsIgnoreCase(status));

            User savedMahasiswa = userRepository.save(mahasiswa);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mahasiswa " + nama + " berhasil ditambahkan");
            response.put("data", mapMahasiswaToResponse(savedMahasiswa));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal menambahkan mahasiswa: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Hapus mahasiswa (soft delete - nonaktifkan)
     */
    @DeleteMapping("/api/mahasiswa/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteMahasiswaApi(@PathVariable String id) {
        try {
            Optional<User> mahasiswaOpt = userRepository.findById(id);

            if (mahasiswaOpt.isEmpty()) {
                return buildErrorResponse("Mahasiswa dengan ID " + id + " tidak ditemukan",
                        HttpStatus.NOT_FOUND);
            }

            User mahasiswa = mahasiswaOpt.get();

            if (!"Mahasiswa".equals(mahasiswa.getRole())) {
                return buildErrorResponse("Hanya dapat menghapus user dengan role Mahasiswa",
                        HttpStatus.BAD_REQUEST);
            }

            // Soft delete: nonaktifkan mahasiswa
            mahasiswa.setActive(false);
            userRepository.save(mahasiswa);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mahasiswa " + mahasiswa.getNama() + " berhasil dinonaktifkan");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal menghapus mahasiswa: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Update status mahasiswa (aktif/nonaktif)
     */
    @PutMapping("/api/mahasiswa/{id}/status")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateMahasiswaStatusApi(
            @PathVariable String id,
            @RequestBody Map<String, String> statusData) {
        try {
            Optional<User> mahasiswaOpt = userRepository.findById(id);

            if (mahasiswaOpt.isEmpty()) {
                return buildErrorResponse("Mahasiswa tidak ditemukan", HttpStatus.NOT_FOUND);
            }

            User mahasiswa = mahasiswaOpt.get();

            if (!"Mahasiswa".equals(mahasiswa.getRole())) {
                return buildErrorResponse("Hanya dapat mengubah status user dengan role Mahasiswa",
                        HttpStatus.BAD_REQUEST);
            }

            String status = statusData.get("status");
            if (status == null) {
                return buildErrorResponse("Status tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }

            boolean newStatus = "aktif".equalsIgnoreCase(status) || "1".equals(status) || "true".equalsIgnoreCase(status);
            mahasiswa.setActive(newStatus);

            userRepository.save(mahasiswa);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Status mahasiswa berhasil diperbarui menjadi " +
                    (newStatus ? "Aktif" : "Nonaktif"));
            response.put("data", mapMahasiswaToResponse(mahasiswa));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal memperbarui status mahasiswa: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Update data mahasiswa
     */
    @PutMapping("/api/mahasiswa/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateMahasiswaApi(
            @PathVariable String id,
            @RequestBody Map<String, String> mahasiswaData) {
        try {
            Optional<User> mahasiswaOpt = userRepository.findById(id);

            if (mahasiswaOpt.isEmpty()) {
                return buildErrorResponse("Mahasiswa tidak ditemukan", HttpStatus.NOT_FOUND);
            }

            User mahasiswa = mahasiswaOpt.get();

            if (!"Mahasiswa".equals(mahasiswa.getRole())) {
                return buildErrorResponse("Hanya dapat mengubah user dengan role Mahasiswa",
                        HttpStatus.BAD_REQUEST);
            }

            if (mahasiswaData.containsKey("nama") && !mahasiswaData.get("nama").trim().isEmpty()) {
                mahasiswa.setNama(mahasiswaData.get("nama").trim());
            }

            if (mahasiswaData.containsKey("email") && !mahasiswaData.get("email").trim().isEmpty()) {
                mahasiswa.setEmail(mahasiswaData.get("email").trim());
            }

            if (mahasiswaData.containsKey("status")) {
                String status = mahasiswaData.get("status");
                mahasiswa.setActive("aktif".equalsIgnoreCase(status) || "1".equals(status) || "true".equalsIgnoreCase(status));
            }

            userRepository.save(mahasiswa);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Data mahasiswa berhasil diperbarui");
            response.put("data", mapMahasiswaToResponse(mahasiswa));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal memperbarui data mahasiswa: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Import mahasiswa dari file Excel/CSV (berfungsi lengkap)
     */
    @PostMapping("/api/mahasiswa/import")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importMahasiswaApi(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return buildErrorResponse("File tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }

            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();

            if (!isValidFileType(contentType, fileName)) {
                return buildErrorResponse(
                        "Format file tidak didukung. Gunakan file CSV (.csv) atau Excel (.xlsx, .xls)",
                        HttpStatus.BAD_REQUEST);
            }

            List<User> importedMahasiswa = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            List<String> successes = new ArrayList<>();

            if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                // Parse file Excel
                parseExcelFile(file, importedMahasiswa, errors, successes, "Mahasiswa");
            } else if (fileName.endsWith(".csv")) {
                // Parse file CSV (akan kita implementasikan nanti)
                parseCSVFile(file, importedMahasiswa, errors, successes, "Mahasiswa");
            }

            // Simpan mahasiswa yang berhasil diimport
            List<Map<String, Object>> savedMahasiswa = new ArrayList<>();
            for (User mahasiswa : importedMahasiswa) {
                try {
                    User saved = userRepository.save(mahasiswa);
                    savedMahasiswa.add(mapMahasiswaToResponse(saved));
                    successes.add("Mahasiswa " + mahasiswa.getNama() + " berhasil diimport");
                } catch (Exception e) {
                    errors.add("Gagal menyimpan mahasiswa " + mahasiswa.getNama() + ": " + e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Import file " + fileName + " selesai");
            response.put("filename", fileName);
            response.put("totalImported", importedMahasiswa.size());
            response.put("successCount", successes.size());
            response.put("errorCount", errors.size());
            response.put("successMessages", successes);
            response.put("errorMessages", errors);
            response.put("importedData", savedMahasiswa);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mengimpor file: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     /**
     * API: Download template Excel untuk import mahasiswa
     */
    @GetMapping("/api/mahasiswa/template")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadMahasiswaTemplate() throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Template Mahasiswa");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("NPM");
            headerRow.createCell(1).setCellValue("Nama");
            headerRow.createCell(2).setCellValue("Email (opsional)");
            
            // Add example data
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("20241001");
            exampleRow.createCell(1).setCellValue("Jane Smith");
            exampleRow.createCell(2).setCellValue("jane.smith@student.unpar.ac.id");
            
            // Auto-size columns
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=template_import_mahasiswa.xlsx")
                    .body(outputStream.toByteArray());
                    
        } catch (Exception e) {
            throw new Exception("Gagal membuat template: " + e.getMessage());
        }
    }
    
   
    /**
     * API: Get mahasiswa by ID (untuk edit form)
     */
    @GetMapping("/api/mahasiswa/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMahasiswaByIdApi(@PathVariable String id) {
        try {
            Optional<User> mahasiswaOpt = userRepository.findById(id);

            if (mahasiswaOpt.isEmpty()) {
                return buildErrorResponse("Mahasiswa tidak ditemukan", HttpStatus.NOT_FOUND);
            }

            User mahasiswa = mahasiswaOpt.get();

            if (!"Mahasiswa".equals(mahasiswa.getRole())) {
                return buildErrorResponse("User bukan mahasiswa", HttpStatus.BAD_REQUEST);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", mapMahasiswaToResponse(mahasiswa));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mengambil data mahasiswa: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ============================
    // API ENDPOINTS - KELOLA MATA KULIAH (AKTIF SAJA)
    // ============================

    /**
     * API: Ambil semua mata kuliah AKTIF (JSON)
     */
    @GetMapping("/api/mata-kuliah")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllMataKuliahApi(
            @RequestParam(required = false, defaultValue = "aktif") String status) {
        try {
            List<MataKuliah> mataKuliahList;
            
            if ("semua".equalsIgnoreCase(status)) {
                mataKuliahList = mataKuliahRepository.findAll();
            } else if ("nonaktif".equalsIgnoreCase(status)) {
                // Mengambil mata kuliah nonaktif
                mataKuliahList = mataKuliahRepository.findAll().stream()
                        .filter(mk -> !mk.isActive())
                        .collect(Collectors.toList());
            } else {
                // Default: ambil hanya yang aktif
                mataKuliahList = mataKuliahRepository.findByIsActiveTrue();
            }

            List<Map<String, Object>> formattedList = mataKuliahList.stream()
                    .map(mk -> {
                        Map<String, Object> mkMap = new HashMap<>();
                        mkMap.put("id", mk.getKodeMK());
                        mkMap.put("kode", mk.getKodeMK());
                        mkMap.put("nama", mk.getNama());
                        mkMap.put("sks", mk.getSks());
                        mkMap.put("semester", getSemesterFromKode(mk.getKodeMK()));
                        mkMap.put("status", mk.isActive() ? "Aktif" : "Nonaktif");
                        mkMap.put("isActive", mk.isActive());
                        return mkMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", formattedList);
            response.put("count", formattedList.size());
            response.put("filter", status);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mengambil data mata kuliah: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Cari mata kuliah AKTIF berdasarkan kode atau nama
     */
    @GetMapping("/api/mata-kuliah/search")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> searchMataKuliahApi(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "aktif") String status) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllMataKuliahApi(status);
            }

            String searchKeyword = keyword.trim().toLowerCase();
            
            // Ambil data berdasarkan filter status
            List<MataKuliah> baseList;
            if ("semua".equalsIgnoreCase(status)) {
                baseList = mataKuliahRepository.findAll();
            } else if ("nonaktif".equalsIgnoreCase(status)) {
                baseList = mataKuliahRepository.findAll().stream()
                        .filter(mk -> !mk.isActive())
                        .collect(Collectors.toList());
            } else {
                baseList = mataKuliahRepository.findByIsActiveTrue();
            }

            // Filter berdasarkan keyword
            List<MataKuliah> results = baseList.stream()
                    .filter(mk ->
                            mk.getKodeMK().toLowerCase().contains(searchKeyword) ||
                            mk.getNama().toLowerCase().contains(searchKeyword))
                    .collect(Collectors.toList());

            List<Map<String, Object>> formattedResults = results.stream()
                    .map(mk -> {
                        Map<String, Object> mkMap = new HashMap<>();
                        mkMap.put("id", mk.getKodeMK());
                        mkMap.put("kode", mk.getKodeMK());
                        mkMap.put("nama", mk.getNama());
                        mkMap.put("sks", mk.getSks());
                        mkMap.put("semester", getSemesterFromKode(mk.getKodeMK()));
                        mkMap.put("status", mk.isActive() ? "Aktif" : "Nonaktif");
                        mkMap.put("isActive", mk.isActive());
                        return mkMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", formattedResults);
            response.put("count", formattedResults.size());
            response.put("filter", status);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mencari mata kuliah: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Tambah mata kuliah baru (default aktif)
     */
    @PostMapping("/api/mata-kuliah")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> addMataKuliahApi(@RequestBody Map<String, Object> mkData) {
        try {
            String kode = (String) mkData.get("kode");
            String nama = (String) mkData.get("nama");

            if (kode == null || kode.trim().isEmpty()) {
                return buildErrorResponse("Kode mata kuliah tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }

            if (nama == null || nama.trim().isEmpty()) {
                return buildErrorResponse("Nama mata kuliah tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }

            kode = kode.trim().toUpperCase();
            nama = nama.trim();

            if (mataKuliahRepository.existsById(kode)) {
                return buildErrorResponse("Kode mata kuliah " + kode + " sudah terdaftar", HttpStatus.BAD_REQUEST);
            }

            int sks = 3;
            try {
                if (mkData.get("sks") != null) {
                    if (mkData.get("sks") instanceof Integer) {
                        sks = (Integer) mkData.get("sks");
                    } else if (mkData.get("sks") instanceof String) {
                        sks = Integer.parseInt(((String) mkData.get("sks")).trim());
                    }

                    if (sks <= 0 || sks > 10) {
                        return buildErrorResponse("SKS harus antara 1-10", HttpStatus.BAD_REQUEST);
                    }
                }
            } catch (NumberFormatException e) {
                return buildErrorResponse("SKS harus berupa angka", HttpStatus.BAD_REQUEST);
            }

            // Default status adalah aktif
            boolean isActive = true;
            if (mkData.containsKey("status") && mkData.get("status") != null) {
                String status = mkData.get("status").toString();
                isActive = !"Nonaktif".equalsIgnoreCase(status);
            }

            MataKuliah mataKuliah = new MataKuliah();
            mataKuliah.setKodeMK(kode);
            mataKuliah.setNama(nama);
            mataKuliah.setSks(sks);
            mataKuliah.setActive(isActive);
            mataKuliah.setTugasList(new HashSet<>());
            mataKuliah.setDosenList(new HashSet<>());
            mataKuliah.setMahasiswaList(new HashSet<>());

            MataKuliah savedMk = mataKuliahRepository.save(mataKuliah);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mata kuliah " + nama + " berhasil ditambahkan");

            Map<String, Object> savedData = new HashMap<>();
            savedData.put("id", savedMk.getKodeMK());
            savedData.put("kode", savedMk.getKodeMK());
            savedData.put("nama", savedMk.getNama());
            savedData.put("sks", savedMk.getSks());
            savedData.put("semester", getSemesterFromKode(savedMk.getKodeMK()));
            savedData.put("status", savedMk.isActive() ? "Aktif" : "Nonaktif");
            savedData.put("isActive", savedMk.isActive());

            response.put("data", savedData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal menambahkan mata kuliah: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Hapus mata kuliah (soft delete - nonaktifkan)
     */
    @DeleteMapping("/api/mata-kuliah/{kode}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteMataKuliahApi(@PathVariable String kode) {
        try {
            Optional<MataKuliah> mkOpt = mataKuliahRepository.findById(kode);

            if (mkOpt.isEmpty()) {
                return buildErrorResponse("Mata kuliah dengan kode " + kode + " tidak ditemukan",
                        HttpStatus.NOT_FOUND);
            }

            MataKuliah mataKuliah = mkOpt.get();
            String namaMk = mataKuliah.getNama();

            // Soft delete: nonaktifkan mata kuliah
            mataKuliah.setActive(false);
            mataKuliahRepository.save(mataKuliah);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Mata kuliah " + namaMk + " berhasil dinonaktifkan");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal menghapus mata kuliah: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Update status mata kuliah (aktif/nonaktif)
     */
    @PutMapping("/api/mata-kuliah/{kode}/status")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateMataKuliahStatusApi(
            @PathVariable String kode,
            @RequestBody Map<String, String> statusData) {
        try {
            Optional<MataKuliah> mkOpt = mataKuliahRepository.findById(kode);

            if (mkOpt.isEmpty()) {
                return buildErrorResponse("Mata kuliah tidak ditemukan", HttpStatus.NOT_FOUND);
            }

            String status = statusData.get("status");
            if (status == null || status.trim().isEmpty()) {
                return buildErrorResponse("Status tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }

            status = status.trim();
            boolean isActive = "Aktif".equalsIgnoreCase(status);

            if (!isActive && !"Nonaktif".equalsIgnoreCase(status)) {
                return buildErrorResponse("Status harus 'Aktif' atau 'Nonaktif'", HttpStatus.BAD_REQUEST);
            }

            MataKuliah mataKuliah = mkOpt.get();
            String oldStatus = mataKuliah.isActive() ? "Aktif" : "Nonaktif";
            mataKuliah.setActive(isActive);

            mataKuliahRepository.save(mataKuliah);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Status mata kuliah " + mataKuliah.getNama() +
                    " berhasil diubah dari " + oldStatus + " menjadi " + status);

            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("id", mataKuliah.getKodeMK());
            updatedData.put("kode", mataKuliah.getKodeMK());
            updatedData.put("nama", mataKuliah.getNama());
            updatedData.put("sks", mataKuliah.getSks());
            updatedData.put("semester", getSemesterFromKode(mataKuliah.getKodeMK()));
            updatedData.put("status", mataKuliah.isActive() ? "Aktif" : "Nonaktif");
            updatedData.put("isActive", mataKuliah.isActive());

            response.put("data", updatedData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal memperbarui status mata kuliah: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Update data mata kuliah
     */
    @PutMapping("/api/mata-kuliah/{kode}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateMataKuliahApi(
            @PathVariable String kode,
            @RequestBody Map<String, Object> mkData) {
        try {
            Optional<MataKuliah> mkOpt = mataKuliahRepository.findById(kode);

            if (mkOpt.isEmpty()) {
                return buildErrorResponse("Mata kuliah tidak ditemukan", HttpStatus.NOT_FOUND);
            }

            MataKuliah mataKuliah = mkOpt.get();
            boolean updated = false;
            String oldKode = mataKuliah.getKodeMK();

            if (mkData.containsKey("kode") && mkData.get("kode") != null) {
                String newKode = ((String) mkData.get("kode")).trim().toUpperCase();
                if (!newKode.isEmpty() && !newKode.equals(oldKode)) {
                    if (mataKuliahRepository.existsById(newKode)) {
                        return buildErrorResponse("Kode " + newKode + " sudah digunakan oleh mata kuliah lain",
                                HttpStatus.BAD_REQUEST);
                    }
                    mataKuliah.setKodeMK(newKode);
                    updated = true;
                }
            }

            if (mkData.containsKey("nama") && mkData.get("nama") != null) {
                String newNama = ((String) mkData.get("nama")).trim();
                if (!newNama.isEmpty() && !newNama.equals(mataKuliah.getNama())) {
                    mataKuliah.setNama(newNama);
                    updated = true;
                }
            }

            if (mkData.containsKey("sks")) {
                try {
                    int newSks;
                    if (mkData.get("sks") instanceof Integer) {
                        newSks = (Integer) mkData.get("sks");
                    } else if (mkData.get("sks") instanceof String) {
                        newSks = Integer.parseInt((String) mkData.get("sks"));
                    } else {
                        newSks = mataKuliah.getSks();
                    }

                    if (newSks > 0 && newSks <= 10 && newSks != mataKuliah.getSks()) {
                        mataKuliah.setSks(newSks);
                        updated = true;
                    }
                } catch (NumberFormatException e) {
                    // Skip jika format salah
                }
            }

            if (mkData.containsKey("status") && mkData.get("status") != null) {
                String newStatus = ((String) mkData.get("status")).trim();
                boolean newIsActive = "Aktif".equalsIgnoreCase(newStatus);
                boolean currentIsActive = mataKuliah.isActive();

                if ((newIsActive != currentIsActive) &&
                        ("Aktif".equalsIgnoreCase(newStatus) || "Nonaktif".equalsIgnoreCase(newStatus))) {
                    mataKuliah.setActive(newIsActive);
                    updated = true;
                }
            }

            if (updated) {
                mataKuliahRepository.save(mataKuliah);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", updated ? "Data mata kuliah berhasil diperbarui" : "Tidak ada perubahan data");

            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("id", mataKuliah.getKodeMK());
            updatedData.put("kode", mataKuliah.getKodeMK());
            updatedData.put("nama", mataKuliah.getNama());
            updatedData.put("sks", mataKuliah.getSks());
            updatedData.put("semester", getSemesterFromKode(mataKuliah.getKodeMK()));
            updatedData.put("status", mataKuliah.isActive() ? "Aktif" : "Nonaktif");
            updatedData.put("isActive", mataKuliah.isActive());

            response.put("data", updatedData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal memperbarui data mata kuliah: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Get mata kuliah by kode (untuk edit form)
     */
    @GetMapping("/api/mata-kuliah/{kode}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMataKuliahByKodeApi(@PathVariable String kode) {
        try {
            Optional<MataKuliah> mkOpt = mataKuliahRepository.findById(kode);

            if (mkOpt.isEmpty()) {
                return buildErrorResponse("Mata kuliah tidak ditemukan", HttpStatus.NOT_FOUND);
            }

            MataKuliah mataKuliah = mkOpt.get();

            Map<String, Object> mkData = new HashMap<>();
            mkData.put("id", mataKuliah.getKodeMK());
            mkData.put("kode", mataKuliah.getKodeMK());
            mkData.put("nama", mataKuliah.getNama());
            mkData.put("sks", mataKuliah.getSks());
            mkData.put("semester", getSemesterFromKode(mataKuliah.getKodeMK()));
            mkData.put("status", mataKuliah.isActive() ? "Aktif" : "Nonaktif");
            mkData.put("isActive", mataKuliah.isActive());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", mkData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mengambil data mata kuliah: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API: Import mata kuliah dari file Excel/CSV (berfungsi lengkap)
     */
    @PostMapping("/api/mata-kuliah/import")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importMataKuliahApi(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return buildErrorResponse("File tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }

            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();

            if (!isValidFileType(contentType, fileName)) {
                return buildErrorResponse(
                        "Format file tidak didukung. Gunakan file CSV (.csv) atau Excel (.xlsx, .xls)",
                        HttpStatus.BAD_REQUEST);
            }

            List<MataKuliah> importedMataKuliah = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            List<String> successes = new ArrayList<>();

            if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                // Parse file Excel untuk mata kuliah
                parseExcelFileForMataKuliah(file, importedMataKuliah, errors, successes);
            } else if (fileName.endsWith(".csv")) {
                // Parse file CSV untuk mata kuliah
                parseCSVFileForMataKuliah(file, importedMataKuliah, errors, successes);
            }

            // Simpan mata kuliah yang berhasil diimport
            List<Map<String, Object>> savedMataKuliah = new ArrayList<>();
            for (MataKuliah mk : importedMataKuliah) {
                try {
                    MataKuliah saved = mataKuliahRepository.save(mk);
                    
                    Map<String, Object> mkData = new HashMap<>();
                    mkData.put("id", saved.getKodeMK());
                    mkData.put("kode", saved.getKodeMK());
                    mkData.put("nama", saved.getNama());
                    mkData.put("sks", saved.getSks());
                    mkData.put("semester", getSemesterFromKode(saved.getKodeMK()));
                    mkData.put("status", saved.isActive() ? "Aktif" : "Nonaktif");
                    mkData.put("isActive", saved.isActive());
                    
                    savedMataKuliah.add(mkData);
                    successes.add("Mata kuliah " + saved.getNama() + " berhasil diimport");
                } catch (Exception e) {
                    errors.add("Gagal menyimpan mata kuliah " + mk.getNama() + ": " + e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Import file " + fileName + " selesai");
            response.put("filename", fileName);
            response.put("totalImported", importedMataKuliah.size());
            response.put("successCount", successes.size());
            response.put("errorCount", errors.size());
            response.put("successMessages", successes);
            response.put("errorMessages", errors);
            response.put("importedData", savedMataKuliah);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return buildErrorResponse("Gagal mengimpor file: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     /**
     * API: Download template Excel untuk import mata kuliah
     */
    @GetMapping("/api/mata-kuliah/template")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadMataKuliahTemplate() throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Template Mata Kuliah");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Kode MK");
            headerRow.createCell(1).setCellValue("Nama Mata Kuliah");
            headerRow.createCell(2).setCellValue("SKS");
            
            // Add example data
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("IF4010");
            exampleRow.createCell(1).setCellValue("Pemrograman Web");
            exampleRow.createCell(2).setCellValue(3);
            
            // Auto-size columns
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=template_import_mata_kuliah.xlsx")
                    .body(outputStream.toByteArray());
                    
        } catch (Exception e) {
            throw new Exception("Gagal membuat template: " + e.getMessage());
        }
    }

    // ============================
    // HELPER METHODS
    // ============================

    private String generateDosenNip() {
        List<User> dosenList = userRepository.findByRole("Dosen");

        int maxNumber = 0;
        for (User dosen : dosenList) {
            String id = dosen.getIdUser();
            try {
                int number = Integer.parseInt(id);
                if (number > maxNumber) {
                    maxNumber = number;
                }
            } catch (NumberFormatException e) {
                //  jika bukan angka maka dilewat
            }
        }

        return String.valueOf(maxNumber + 1);
    }

    private String extractNipFromId(String id) {
        if (id.startsWith("DSN")) {
            return id.substring(3);
        }
        return id;
    }

    private String generateEmail(String nama, String nip) {
        String emailName = nama.toLowerCase()
                .replace(" ", ".")
                .replace("dr.", "")
                .replace(",", "")
                .trim();

        emailName = emailName.replaceAll("[^a-z.]", "");

        return emailName + "@unpar.ac.id";
    }

    private Map<String, Object> mapDosenToResponse(User dosen) {
        Map<String, Object> dosenMap = new HashMap<>();
        dosenMap.put("id", dosen.getIdUser());
        dosenMap.put("nip", extractNipFromId(dosen.getIdUser()));
        dosenMap.put("nama", dosen.getNama());
        dosenMap.put("email", dosen.getEmail());
        dosenMap.put("role", dosen.getRole());
        dosenMap.put("status", dosen.isActive() ? "Aktif" : "Nonaktif");
        dosenMap.put("isActive", dosen.isActive());
        return dosenMap;
    }

    private String generateMahasiswaNpm() {
        List<User> mahasiswaList = userRepository.findByRole("Mahasiswa");

        int currentYear = LocalDate.now().getYear();
        int maxSequence = 0;

        for (User mahasiswa : mahasiswaList) {
            String npm = mahasiswa.getIdUser();
            if (npm.length() == 7 && npm.startsWith(String.valueOf(currentYear))) {
                try {
                    int sequence = Integer.parseInt(npm.substring(4));
                    if (sequence > maxSequence) {
                        maxSequence = sequence;
                    }
                } catch (NumberFormatException e) {
                    // Skip jika bukan angka
                }
            }
        }

        return String.format("%d%03d", currentYear, maxSequence + 1);
    }

    private String extractNpmFromId(String id) {
        return id;
    }

    private String generateEmailMahasiswa(String nama, String npm) {
        String emailName = nama.toLowerCase()
                .replace(" ", ".")
                .replace(",", "")
                .trim();

        emailName = emailName.replaceAll("[^a-z.]", "");

        if (emailName.length() > 20) {
            emailName = emailName.substring(0, 20);
        }

        return emailName + "@student.unpar.ac.id";
    }

    private Map<String, Object> mapMahasiswaToResponse(User mahasiswa) {
        Map<String, Object> mahasiswaMap = new HashMap<>();
        mahasiswaMap.put("id", mahasiswa.getIdUser());
        mahasiswaMap.put("npm", extractNpmFromId(mahasiswa.getIdUser()));
        mahasiswaMap.put("nama", mahasiswa.getNama());
        mahasiswaMap.put("email", mahasiswa.getEmail());
        mahasiswaMap.put("role", mahasiswa.getRole());
        mahasiswaMap.put("status", mahasiswa.isActive() ? "Aktif" : "Nonaktif");
        mahasiswaMap.put("isActive", mahasiswa.isActive());
        return mahasiswaMap;
    }

    private String getSemesterFromKode(String kodeMk) {
        if (kodeMk == null || kodeMk.length() < 2) {
            return "-";
        }
        
        try {
            char semesterChar = kodeMk.charAt(1);
            if (Character.isDigit(semesterChar)) {
                int semester = Character.getNumericValue(semesterChar);
                return "Semester " + semester;
            }
        } catch (Exception e) {
            // Ignore error
        }
        
        return "-";
    }

    private boolean isValidFileType(String contentType, String fileName) {
        if (contentType == null) {
            return fileName != null &&
                    (fileName.endsWith(".csv") ||
                            fileName.endsWith(".xlsx") ||
                            fileName.endsWith(".xls"));
        }

        return contentType.equals("text/csv") ||
                contentType.equals("application/vnd.ms-excel") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        error.put("status", status.value());
        return ResponseEntity.status(status).body(error);
    }

    /**
     * API: Test endpoint untuk debugging
     */
    @GetMapping("/api/dosen/test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testDosenApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin Dosen API is working!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("totalDosen", userRepository.findByRole("Dosen").size());
        response.put("dosenAktif", userRepository.findByRoleAndIsActiveTrue("Dosen").size());
        return ResponseEntity.ok(response);
    }

    /**
     * API: Test endpoint untuk debugging mahasiswa
     */
    @GetMapping("/api/mahasiswa/test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testMahasiswaApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin Mahasiswa API is working!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("totalMahasiswa", userRepository.findByRole("Mahasiswa").size());
        response.put("mahasiswaAktif", userRepository.findByRoleAndIsActiveTrue("Mahasiswa").size());
        return ResponseEntity.ok(response);
    }

    /**
     * API: Test endpoint untuk debugging
     */
    @GetMapping("/api/mata-kuliah/test")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testMataKuliahApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin Mata Kuliah API is working!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("totalMataKuliah", mataKuliahRepository.count());
        try {
            response.put("mataKuliahAktif", mataKuliahRepository.findByIsActiveTrue().size());
        } catch (Exception e) {
            response.put("mataKuliahAktif", "Method not available");
        }
        return ResponseEntity.ok(response);
    }

     // ============================
    // HELPER METHODS FOR EXCEL PARSING
    // ============================

    /**
     * Parse file Excel untuk dosen atau mahasiswa
     */
    private void parseExcelFile(MultipartFile file, List<User> userList, 
            List<String> errors, List<String> successes, String role) throws Exception {
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0); // Ambil sheet pertama
            Iterator<Row> rowIterator = sheet.iterator();
            
            int rowNumber = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowNumber++;
                
                // Skip header row (row pertama)
                if (rowNumber == 1) {
                    continue;
                }
                
                try {
                    // Ambil data dari setiap kolom
                    String id = getCellValueAsString(row.getCell(0));
                    String nama = getCellValueAsString(row.getCell(1));
                    String email = getCellValueAsString(row.getCell(2));
                    
                    // Validasi data
                    if (id == null || id.trim().isEmpty()) {
                        errors.add("Baris " + rowNumber + ": ID/NIP/NPM tidak boleh kosong");
                        continue;
                    }
                    
                    if (nama == null || nama.trim().isEmpty()) {
                        errors.add("Baris " + rowNumber + ": Nama tidak boleh kosong");
                        continue;
                    }
                    
                    // Cek apakah sudah ada di database
                    if (userRepository.existsById(id)) {
                        errors.add("Baris " + rowNumber + ": " + id + " sudah terdaftar");
                        continue;
                    }
                    
                    // Buat user baru
                    User user = new User();
                    user.setIdUser(id.trim());
                    user.setNama(nama.trim());
                    
                    if (email != null && !email.trim().isEmpty()) {
                        user.setEmail(email.trim());
                    } else {
                        // Generate email otomatis
                        if ("Dosen".equals(role)) {
                            user.setEmail(generateEmail(nama, id));
                        } else {
                            user.setEmail(generateEmailMahasiswa(nama, id));
                        }
                    }
                    
                    // Set password default (password123)
                    String defaultPassword = "$2a$10$lpXunJk2Te8/hHcfFFmpduViPATPUYuau.rAK1ckJbpDh5m8MSXV2";
                    user.setPassword(defaultPassword);
                    user.setRole(role);
                    user.setActive(true); // Default aktif
                    
                    userList.add(user);
                    successes.add("Baris " + rowNumber + ": " + nama + " berhasil diparsing");
                    
                } catch (Exception e) {
                    errors.add("Baris " + rowNumber + ": Error - " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            throw new Exception("Error parsing file Excel: " + e.getMessage());
        }
    }
    
    /**
     * Parse file Excel untuk mata kuliah
     */
    private void parseExcelFileForMataKuliah(MultipartFile file, List<MataKuliah> mkList,
            List<String> errors, List<String> successes) throws Exception {
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0); // Ambil sheet pertama
            Iterator<Row> rowIterator = sheet.iterator();
            
            int rowNumber = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowNumber++;
                
                // Skip header row (row pertama)
                if (rowNumber == 1) {
                    continue;
                }
                
                try {
                    // Ambil data dari setiap kolom
                    String kode = getCellValueAsString(row.getCell(0));
                    String nama = getCellValueAsString(row.getCell(1));
                    String sksStr = getCellValueAsString(row.getCell(2));
                    
                    // Validasi data
                    if (kode == null || kode.trim().isEmpty()) {
                        errors.add("Baris " + rowNumber + ": Kode mata kuliah tidak boleh kosong");
                        continue;
                    }
                    
                    if (nama == null || nama.trim().isEmpty()) {
                        errors.add("Baris " + rowNumber + ": Nama mata kuliah tidak boleh kosong");
                        continue;
                    }
                    
                    // Cek apakah sudah ada di database
                    if (mataKuliahRepository.existsById(kode.trim())) {
                        errors.add("Baris " + rowNumber + ": Kode " + kode + " sudah terdaftar");
                        continue;
                    }
                    
                    // Parse SKS
                    int sks = 3; // Default
                    if (sksStr != null && !sksStr.trim().isEmpty()) {
                        try {
                            sks = Integer.parseInt(sksStr.trim());
                            if (sks <= 0 || sks > 10) {
                                errors.add("Baris " + rowNumber + ": SKS harus antara 1-10");
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            errors.add("Baris " + rowNumber + ": Format SKS tidak valid");
                            continue;
                        }
                    }
                    
                    // Buat mata kuliah baru
                    MataKuliah mk = new MataKuliah();
                    mk.setKodeMK(kode.trim().toUpperCase());
                    mk.setNama(nama.trim());
                    mk.setSks(sks);
                    mk.setActive(true); // Default aktif
                    mk.setTugasList(new HashSet<>());
                    mk.setDosenList(new HashSet<>());
                    mk.setMahasiswaList(new HashSet<>());
                    
                    mkList.add(mk);
                    successes.add("Baris " + rowNumber + ": " + nama + " berhasil diparsing");
                    
                } catch (Exception e) {
                    errors.add("Baris " + rowNumber + ": Error - " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            throw new Exception("Error parsing file Excel: " + e.getMessage());
        }
    }
    
    /**
     * Helper method untuk membaca nilai sel Excel
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Format numeric tanpa desimal jika integer
                    double num = cell.getNumericCellValue();
                    if (num == (int) num) {
                        return String.valueOf((int) num);
                    } else {
                        return String.valueOf(num);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
    
    /**
     * Parse file CSV untuk dosen atau mahasiswa (placeholder)
     */
    private void parseCSVFile(MultipartFile file, List<User> userList,
            List<String> errors, List<String> successes, String role) throws Exception {
        // Implementasi parsing CSV bisa ditambahkan di sini
        throw new Exception("Parsing CSV belum diimplementasikan. Gunakan file Excel (.xlsx)");
    }
    
    /**
     * Parse file CSV untuk mata kuliah (placeholder)
     */
    private void parseCSVFileForMataKuliah(MultipartFile file, List<MataKuliah> mkList,
            List<String> errors, List<String> successes) throws Exception {
        // Implementasi parsing CSV bisa ditambahkan di sini
        throw new Exception("Parsing CSV belum diimplementasikan. Gunakan file Excel (.xlsx)");

    }
    @GetMapping("/matakuliah-kelas-detail")
    public String matakuliahKelasDetail(@RequestParam String kode, 
                                    @AuthenticationPrincipal CustomUserDetails user, 
                                    Model model) {
        if (kode == null || kode.isEmpty()) {
            return "redirect:/admin/kelola-mata-kuliah";
        }

        MataKuliah mk = mataKuliahRepository.findById(kode).orElse(null);
        if (mk == null) {
            return "redirect:/admin/kelola-mata-kuliah";
        }

        // Consistent gradient index based on kode
        int gradientCount = 4;
        int colorIndex = Math.abs(kode.hashCode()) % gradientCount;
        model.addAttribute("colorIndex", colorIndex);

        // Find coordinator (first active MataKuliahDosen for this MK)
        MataKuliahDosen koordinator = null;
        try {
            List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kode, true);
            if (!dosenList.isEmpty()) {
                koordinator = dosenList.get(0);
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinator: " + e.getMessage());
        }
        model.addAttribute("koordinator", koordinator);

        // Fetch tugas besar for this mata kuliah
        List<TugasBesar> tugasList = Collections.emptyList();
        try {
            tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(kode, true);
        } catch (Exception e) {
            System.err.println("Error fetching tasks for MK: " + e.getMessage());
        }

        // Fetch all classes (kelasList - MataKuliahDosen entries)
        List<MataKuliahDosen> kelasList = Collections.emptyList();
        try {
            kelasList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kode, true);
        } catch (Exception e) {
            System.err.println("Error fetching classes for MK: " + e.getMessage());
        }

        model.addAttribute("mkDetail", mk);
        model.addAttribute("tugasList", tugasList);
        model.addAttribute("kelasList", kelasList);
        model.addAttribute("user", user);

        return "admin/matakuliah-kelas-detail"; 
    }

    @GetMapping("/matakuliah-detail")
    public String matakuliahDetail(@RequestParam String kode, @AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (kode == null || kode.isEmpty()) {
            return "redirect:/admin/matakuliah-kelas-detail";
        }

        MataKuliah mk = mataKuliahRepository.findById(kode).orElse(null);
        if (mk == null) {
            return "redirect:/admin/matakuliah-kelas-detail";
        }

        // Consistent gradient index based on kode
        int gradientCount = 4;
        int colorIndex = Math.abs(kode.hashCode()) % gradientCount;
        model.addAttribute("colorIndex", colorIndex);

        // Find coordinator (first active MataKuliahDosen for this MK)
        MataKuliahDosen koordinator = null;
        try {
            List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kode, true);
            if (!dosenList.isEmpty()) {
                koordinator = dosenList.get(0);
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinator: " + e.getMessage());
        }
        model.addAttribute("koordinator", koordinator);

        // Fetch tugas besar for this mata kuliah
        List<TugasBesar> tugasList = Collections.emptyList();
        try {
            tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(kode, true);
        } catch (Exception e) {
            System.err.println("Error fetching tasks for MK: " + e.getMessage());
        }

        // Fetch all classes (kelasList - MataKuliahDosen entries)
        List<MataKuliahDosen> kelasList = Collections.emptyList();
        try {
            kelasList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kode, true);
        } catch (Exception e) {
            System.err.println("Error fetching classes for MK: " + e.getMessage());
        }

        model.addAttribute("mkDetail", mk);
        model.addAttribute("tugasList", tugasList);
        model.addAttribute("kelasList", kelasList);
        model.addAttribute("user", user);

        return "admin/matakuliah-detail";
    }
   

@GetMapping("/peserta-detail")
public String detailPeserta(@RequestParam String kodeMk, @RequestParam(required = false) Integer colorIndex,
                            @AuthenticationPrincipal CustomUserDetails user,
                            Model model) {
    model.addAttribute("user", user);

    MataKuliah mk = mataKuliahRepository.findById(kodeMk).orElse(null);
    if (mk == null) {
        return "redirect:/admin/dashboard";
    }

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
        model.addAttribute("colorIndex", finalColorIndex);

    // Ambil semua dosen aktif untuk MK ini
    List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);

    // Tentukan koordinator (misalnya ambil index pertama)
    MataKuliahDosen koordinator = dosenList.isEmpty() ? null : dosenList.get(0);

    // Ambil mahasiswa aktif
    List<MataKuliahMahasiswa> mahasiswaList = mkMahasiswaRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);

    // Gabungkan ke DTO untuk view
    List<PesertaMatkulDTO> combinedPesertaList = new ArrayList<>();
    int no = 1;

    // Tambahkan koordinator
    if (koordinator != null) {
        combinedPesertaList.add(new PesertaMatkulDTO(no++, koordinator.getUser().getNama(),
                koordinator.getUser().getIdUser(), "Koordinator"));
    }

    // Tambahkan dosen pengampu lain (selain koordinator)
    for (int i = 1; i < dosenList.size(); i++) {
        User dosenUser = dosenList.get(i).getUser();
        combinedPesertaList.add(new PesertaMatkulDTO(no++, dosenUser.getNama(),
                dosenUser.getIdUser(), "Pengampu"));
    }

    // Tambahkan mahasiswa
    for (MataKuliahMahasiswa mhs : mahasiswaList) {
        combinedPesertaList.add(new PesertaMatkulDTO(no++, mhs.getUser().getNama(),
                mhs.getUser().getIdUser(), "Mahasiswa"));
    }

    model.addAttribute("mkDetail", mk);
    model.addAttribute("koordinator", koordinator);
    model.addAttribute("combinedPesertaList", combinedPesertaList);
    model.addAttribute("pesertaCount", mahasiswaList.size());

    return "admin/peserta-detail";
}
}
