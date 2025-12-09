package com.Tubeslayer.controller;

import com.Tubeslayer.entity.MataKuliah;
import com.Tubeslayer.entity.User;
import com.Tubeslayer.repository.MataKuliahRepository;
import com.Tubeslayer.repository.UserRepository;
import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DashboardAdminService dashboardService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MataKuliahRepository mataKuliahRepository;

    public AdminController(DashboardAdminService dashboardService) {
        this.dashboardService = dashboardService;
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

        long jumlahMk = dashboardService.getJumlahMkAktifUniversal();
        long jumlahTb = dashboardService.getJumlahTbAktifUniversal();

        model.addAttribute("jumlahMk", jumlahMk);
        model.addAttribute("jumlahTb", jumlahTb);

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
        
        // Ambil data dosen untuk ditampilkan di view
        List<User> dosenList = userRepository.findByRole("Dosen");
        model.addAttribute("dosenList", dosenList);
        model.addAttribute("dosenCount", dosenList.size());
        
        return "admin/kelola-dosen";
    }

    @GetMapping("/kelola-mahasiswa")
    public String kelolaMahasiswa(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "admin/kelola-mahasiswa";
    }

    // ============================
    // API ENDPOINTS - KELOLA DOSEN
    // ============================
    
    /**
     * API: Ambil semua dosen (JSON)
     */
    @GetMapping("/api/dosen")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllDosenApi() {
        try {
            List<User> dosenList = userRepository.findByRole("Dosen");
            
            // Format response untuk frontend
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
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("Gagal mengambil data dosen: " + e.getMessage(), 
                                     HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * API: Cari dosen berdasarkan nama atau NIP
     */
    @GetMapping("/api/dosen/search")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> searchDosenApi(@RequestParam String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllDosenApi();
            }
            
            // Cari berdasarkan nama (case-insensitive)
            List<User> results = userRepository.findByRoleAndNamaContainingIgnoreCase("Dosen", keyword.trim());
            
            // Juga cari berdasarkan ID jika tidak ditemukan di nama
            if (results.isEmpty()) {
                Optional<User> byId = userRepository.findById(keyword);
                if (byId.isPresent() && "Dosen".equals(byId.get().getRole())) {
                    results.add(byId.get());
                }
            }
            
            List<Map<String, Object>> formattedResults = results.stream()
                .map(dosen -> {
                    Map<String, Object> dosenMap = new HashMap<>();
                    dosenMap.put("id", dosen.getIdUser());
                    dosenMap.put("nip", extractNipFromId(dosen.getIdUser()));
                    dosenMap.put("nama", dosen.getNama());
                    dosenMap.put("email", dosen.getEmail());
                    dosenMap.put("status", dosen.isActive() ? "Aktif" : "Nonaktif");
                    return dosenMap;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", formattedResults);
            response.put("count", formattedResults.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("Gagal mencari dosen: " + e.getMessage(), 
                                     HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * API: Tambah dosen baru
     */
    @PostMapping("/api/dosen")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> addDosenApi(@RequestBody Map<String, String> dosenData) {
        try {
            // Validasi input
            String nama = dosenData.get("nama");
            if (nama == null || nama.trim().isEmpty()) {
                return buildErrorResponse("Nama dosen tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }
            
            nama = nama.trim();
            
            // Generate atau ambil NIP
            String nip = dosenData.getOrDefault("nip", "");
            if (nip.isEmpty()) {
                // Generate NIP otomatis
                nip = generateDosenNip();
            }
            
            // Cek jika NIP sudah ada
            if (userRepository.existsById(nip)) {
                return buildErrorResponse("NIP " + nip + " sudah terdaftar", HttpStatus.BAD_REQUEST);
            }
            
            User dosen = new User();
            dosen.setIdUser(nip);
            dosen.setNama(nama);
            
            // Generate email jika tidak disediakan
            String email = dosenData.getOrDefault("email", "");
            if (email.isEmpty()) {
                email = generateEmail(nama, nip);
            }
            dosen.setEmail(email);
            
            // Password default (default123)
            String defaultPassword = "$2a$10$lpXunJk2Te8/hHcfFFmpduViPATPUYuau.rAK1ckJbpDh5m8MSXV2";
            dosen.setPassword(defaultPassword);
            dosen.setRole("Dosen");
            
            // Status aktif
            String status = dosenData.getOrDefault("status", "1");
            dosen.setActive("1".equals(status) || "aktif".equalsIgnoreCase(status) || "true".equalsIgnoreCase(status));
            
            // Simpan ke database
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
     * API: Hapus dosen
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
            
            // Validasi role
            if (!"Dosen".equals(dosen.getRole())) {
                return buildErrorResponse("Hanya dapat menghapus user dengan role Dosen", 
                                         HttpStatus.BAD_REQUEST);
            }
            
            // Hapus dari database
            userRepository.delete(dosen);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Dosen " + dosen.getNama() + " berhasil dihapus");
            
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
            
            boolean newStatus = "1".equals(status) || "aktif".equalsIgnoreCase(status) || "true".equalsIgnoreCase(status);
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
            
            // Update nama jika ada
            if (dosenData.containsKey("nama") && !dosenData.get("nama").trim().isEmpty()) {
                dosen.setNama(dosenData.get("nama").trim());
            }
            
            // Update email jika ada
            if (dosenData.containsKey("email") && !dosenData.get("email").trim().isEmpty()) {
                dosen.setEmail(dosenData.get("email").trim());
            }
            
            // Update status jika ada
            if (dosenData.containsKey("status")) {
                String status = dosenData.get("status");
                dosen.setActive("1".equals(status) || "aktif".equalsIgnoreCase(status) || "true".equalsIgnoreCase(status));
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
     * API: Import dosen dari file Excel/CSV
     */
    @PostMapping("/api/dosen/import")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importDosenApi(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return buildErrorResponse("File tidak boleh kosong", HttpStatus.BAD_REQUEST);
            }
            
            // Validasi tipe file
            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();
            
            if (!isValidFileType(contentType, fileName)) {
                return buildErrorResponse(
                    "Format file tidak didukung. Gunakan file CSV (.csv) atau Excel (.xlsx, .xls)", 
                    HttpStatus.BAD_REQUEST
                );
            }
            
            // TODO: Implementasi parsing Excel/CSV
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File " + fileName + " berhasil diupload. " +
                         "Fitur parsing data akan segera tersedia.");
            response.put("filename", fileName);
            response.put("size", file.getSize());
            response.put("contentType", contentType);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("Gagal mengimpor file: " + e.getMessage(), 
                                     HttpStatus.INTERNAL_SERVER_ERROR);
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
        return ResponseEntity.ok(response);
    }

    // ============================
    // HELPER METHODS
    // ============================
    
    private String generateDosenNip() {
        List<User> dosenList = userRepository.findByRole("Dosen");
        
        // Cari ID terbesar untuk dosen (format angka)
        int maxNumber = 0;
        for (User dosen : dosenList) {
            String id = dosen.getIdUser();
            try {
                // Coba parse sebagai angka
                int number = Integer.parseInt(id);
                if (number > maxNumber) {
                    maxNumber = number;
                }
            } catch (NumberFormatException e) {
                // Skip jika bukan angka
            }
        }
        
        // Generate ID berikutnya
        return String.valueOf(maxNumber + 1);
    }
    
    private String extractNipFromId(String id) {
        // Jika ID berupa DSN001, kembalikan 001
        if (id.startsWith("DSN")) {
            return id.substring(3);
        }
        return id;
    }
    
    private String generateEmail(String nama, String nip) {
        // Format: nama.tanpa.spasi@unpar.ac.id
        String emailName = nama.toLowerCase()
            .replace(" ", ".")
            .replace("dr.", "")
            .replace(",", "")
            .trim();
        
        // Jika ada karakter khusus, bersihkan
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
    
    private boolean isValidFileType(String contentType, String fileName) {
        if (contentType == null) {
            // Fallback: check by extension
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

    
// ============================
// API ENDPOINTS - KELOLA MAHASISWA
// ============================

/**
 * API: Ambil semua mahasiswa (JSON)
 */
@GetMapping("/api/mahasiswa")
@ResponseBody
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Object>> getAllMahasiswaApi() {
    try {
        List<User> mahasiswaList = userRepository.findByRole("Mahasiswa");
        
        // Format response untuk frontend
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
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        return buildErrorResponse("Gagal mengambil data mahasiswa: " + e.getMessage(), 
                                 HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * API: Cari mahasiswa berdasarkan nama atau NPM
 */
@GetMapping("/api/mahasiswa/search")
@ResponseBody
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Object>> searchMahasiswaApi(@RequestParam String keyword) {
    try {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMahasiswaApi();
        }
        
        // Cari berdasarkan nama (case-insensitive)
        List<User> results = userRepository.findByRoleAndNamaContainingIgnoreCase("Mahasiswa", keyword.trim());
        
        // Juga cari berdasarkan ID jika tidak ditemukan di nama
        if (results.isEmpty()) {
            Optional<User> byId = userRepository.findById(keyword);
            if (byId.isPresent() && "Mahasiswa".equals(byId.get().getRole())) {
                results.add(byId.get());
            }
        }
        
        List<Map<String, Object>> formattedResults = results.stream()
            .map(mahasiswa -> {
                Map<String, Object> mahasiswaMap = new HashMap<>();
                mahasiswaMap.put("id", mahasiswa.getIdUser());
                mahasiswaMap.put("npm", extractNpmFromId(mahasiswa.getIdUser()));
                mahasiswaMap.put("nama", mahasiswa.getNama());
                mahasiswaMap.put("email", mahasiswa.getEmail());
                mahasiswaMap.put("status", mahasiswa.isActive() ? "Aktif" : "Nonaktif");
                return mahasiswaMap;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", formattedResults);
        response.put("count", formattedResults.size());
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        return buildErrorResponse("Gagal mencari mahasiswa: " + e.getMessage(), 
                                 HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * API: Tambah mahasiswa baru
 */
@PostMapping("/api/mahasiswa")
@ResponseBody
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Object>> addMahasiswaApi(@RequestBody Map<String, String> mahasiswaData) {
    try {
        // Validasi input
        String nama = mahasiswaData.get("nama");
        if (nama == null || nama.trim().isEmpty()) {
            return buildErrorResponse("Nama mahasiswa tidak boleh kosong", HttpStatus.BAD_REQUEST);
        }
        
        nama = nama.trim();
        
        // Generate atau ambil NPM
        String npm = mahasiswaData.getOrDefault("npm", "");
        if (npm.isEmpty()) {
            // Generate NPM otomatis
            npm = generateMahasiswaNpm();
        }
        
        // Cek jika NPM sudah ada
        if (userRepository.existsById(npm)) {
            return buildErrorResponse("NPM " + npm + " sudah terdaftar", HttpStatus.BAD_REQUEST);
        }
        
        User mahasiswa = new User();
        mahasiswa.setIdUser(npm);
        mahasiswa.setNama(nama);
        
        // Generate email jika tidak disediakan
        String email = mahasiswaData.getOrDefault("email", "");
        if (email.isEmpty()) {
            email = generateEmailMahasiswa(nama, npm);
        }
        mahasiswa.setEmail(email);
        
        // Password default (default123)
        String defaultPassword = "$2a$10$lpXunJk2Te8/hHcfFFmpduViPATPUYuau.rAK1ckJbpDh5m8MSXV2";
        mahasiswa.setPassword(defaultPassword);
        mahasiswa.setRole("Mahasiswa");
        
        // Status aktif
        String status = mahasiswaData.getOrDefault("status", "1");
        mahasiswa.setActive("1".equals(status) || "aktif".equalsIgnoreCase(status) || "true".equalsIgnoreCase(status));
        
        // Simpan ke database
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
 * API: Hapus mahasiswa
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
        
        // Validasi role
        if (!"Mahasiswa".equals(mahasiswa.getRole())) {
            return buildErrorResponse("Hanya dapat menghapus user dengan role Mahasiswa", 
                                     HttpStatus.BAD_REQUEST);
        }
        
        // Hapus dari database
        userRepository.delete(mahasiswa);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Mahasiswa " + mahasiswa.getNama() + " berhasil dihapus");
        
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
        
        boolean newStatus = "1".equals(status) || "aktif".equalsIgnoreCase(status) || "true".equalsIgnoreCase(status);
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
        
        // Update nama jika ada
        if (mahasiswaData.containsKey("nama") && !mahasiswaData.get("nama").trim().isEmpty()) {
            mahasiswa.setNama(mahasiswaData.get("nama").trim());
        }
        
        // Update email jika ada
        if (mahasiswaData.containsKey("email") && !mahasiswaData.get("email").trim().isEmpty()) {
            mahasiswa.setEmail(mahasiswaData.get("email").trim());
        }
        
        // Update status jika ada
        if (mahasiswaData.containsKey("status")) {
            String status = mahasiswaData.get("status");
            mahasiswa.setActive("1".equals(status) || "aktif".equalsIgnoreCase(status) || "true".equalsIgnoreCase(status));
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
 * API: Import mahasiswa dari file Excel/CSV
 */
@PostMapping("/api/mahasiswa/import")
@ResponseBody
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Object>> importMahasiswaApi(@RequestParam("file") MultipartFile file) {
    try {
        if (file.isEmpty()) {
            return buildErrorResponse("File tidak boleh kosong", HttpStatus.BAD_REQUEST);
        }
        
        // Validasi tipe file
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        
        if (!isValidFileType(contentType, fileName)) {
            return buildErrorResponse(
                "Format file tidak didukung. Gunakan file CSV (.csv) atau Excel (.xlsx, .xls)", 
                HttpStatus.BAD_REQUEST
            );
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "File " + fileName + " berhasil diupload. " +
                     "Fitur parsing data akan segera tersedia.");
        response.put("filename", fileName);
        response.put("size", file.getSize());
        response.put("contentType", contentType);
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        return buildErrorResponse("Gagal mengimpor file: " + e.getMessage(), 
                                 HttpStatus.INTERNAL_SERVER_ERROR);
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
    return ResponseEntity.ok(response);
}

// ============================
// HELPER METHODS untuk MAHASISWA
// ============================

private String generateMahasiswaNpm() {
    List<User> mahasiswaList = userRepository.findByRole("Mahasiswa");
    
    // Cari NPM terbesar untuk mahasiswa (format: tahun + urutan)
    int currentYear = LocalDate.now().getYear();
    int maxSequence = 0;
    
    for (User mahasiswa : mahasiswaList) {
        String npm = mahasiswa.getIdUser();
        // Format NPM: tahun (4 digit) + urutan (3 digit) => total 7 digit
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
    
    // Generate NPM berikutnya
    return String.format("%d%03d", currentYear, maxSequence + 1);
}

private String extractNpmFromId(String id) {
    // Jika ID berupa format lain, sesuaikan
    return id; // NPM langsung sebagai ID
}

private String generateEmailMahasiswa(String nama, String npm) {
    // Format: nama.tanpa.spasi@student.unpar.ac.id
    String emailName = nama.toLowerCase()
        .replace(" ", ".")
        .replace(",", "")
        .trim();
    
    // Jika ada karakter khusus, bersihkan
    emailName = emailName.replaceAll("[^a-z.]", "");
    
    // Potong jika terlalu panjang
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

// ============================
// API ENDPOINTS - KELOLA MATA KULIAH
// ============================

// DI AdminController.java - Bagian Kelola Mata Kuliah

/**
 * API: Ambil semua mata kuliah (JSON)
 */
@GetMapping("/api/mata-kuliah")
@ResponseBody
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Object>> getAllMataKuliahApi() {
    try {
        List<MataKuliah> mataKuliahList = mataKuliahRepository.findAll();
        
        // Format response untuk frontend sesuai dengan entity
        List<Map<String, Object>> formattedList = mataKuliahList.stream()
            .map(mk -> {
                Map<String, Object> mkMap = new HashMap<>();
                mkMap.put("id", mk.getKodeMK()); // ID adalah kodeMK
                mkMap.put("kode", mk.getKodeMK());
                mkMap.put("nama", mk.getNama());
                mkMap.put("sks", mk.getSks());
                // Jika ada field semester di database, tambahkan di sini
                // mkMap.put("semester", mk.getSemester());
                mkMap.put("semester", "-"); // Default jika tidak ada
                mkMap.put("status", mk.isActive() ? "Aktif" : "Nonaktif");
                mkMap.put("isActive", mk.isActive());
                return mkMap;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", formattedList);
        response.put("count", formattedList.size());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        return buildErrorResponse("Gagal mengambil data mata kuliah: " + e.getMessage(), 
                                 HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * API: Cari mata kuliah berdasarkan kode atau nama
 */
@GetMapping("/api/mata-kuliah/search")
@ResponseBody
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Object>> searchMataKuliahApi(@RequestParam String keyword) {
    try {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllMataKuliahApi();
        }
        
        String searchKeyword = keyword.trim();
        
        // Cari berdasarkan kode atau nama (case-insensitive)
        // Pilih salah satu berdasarkan method yang ada di repository Anda:
        
        // OPTION 1: Jika repository punya method findByKodeMKContainingIgnoreCaseOrNamaContainingIgnoreCase
        // List<MataKuliah> results = mataKuliahRepository
        //     .findByKodeMKContainingIgnoreCaseOrNamaContainingIgnoreCase(searchKeyword, searchKeyword);
        
        // OPTION 2: Jika repository hanya punya basic method
        // Cari di semua data
        List<MataKuliah> allMk = mataKuliahRepository.findAll();
        List<MataKuliah> results = allMk.stream()
            .filter(mk -> 
                mk.getKodeMK().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                mk.getNama().toLowerCase().contains(searchKeyword.toLowerCase())
            )
            .collect(Collectors.toList());
        
        List<Map<String, Object>> formattedResults = results.stream()
            .map(mk -> {
                Map<String, Object> mkMap = new HashMap<>();
                mkMap.put("id", mk.getKodeMK());
                mkMap.put("kode", mk.getKodeMK());
                mkMap.put("nama", mk.getNama());
                mkMap.put("sks", mk.getSks());
                mkMap.put("semester", "-");
                mkMap.put("status", mk.isActive() ? "Aktif" : "Nonaktif");
                mkMap.put("isActive", mk.isActive());
                return mkMap;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", formattedResults);
        response.put("count", formattedResults.size());
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        return buildErrorResponse("Gagal mencari mata kuliah: " + e.getMessage(), 
                                 HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/**
 * API: Tambah mata kuliah baru
 */
@PostMapping("/api/mata-kuliah")
@ResponseBody
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Object>> addMataKuliahApi(@RequestBody Map<String, Object> mkData) {
    try {
        // Validasi input
        String kode = (String) mkData.get("kode");
        String nama = (String) mkData.get("nama");
        
        if (kode == null || kode.trim().isEmpty()) {
            return buildErrorResponse("Kode mata kuliah tidak boleh kosong", HttpStatus.BAD_REQUEST);
        }
        
        if (nama == null || nama.trim().isEmpty()) {
            return buildErrorResponse("Nama mata kuliah tidak boleh kosong", HttpStatus.BAD_REQUEST);
        }
        
        // Cek jika kode sudah ada
        if (mataKuliahRepository.existsById(kode)) {
            return buildErrorResponse("Kode mata kuliah " + kode + " sudah terdaftar", HttpStatus.BAD_REQUEST);
        }
        if (mataKuliahRepository.existsById(kode)) {
            return buildErrorResponse("Kode mata kuliah " + kode + " sudah terdaftar", HttpStatus.BAD_REQUEST);
        }
        
        // Validasi SKS
        int sks = 3; // default
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
        
        // Status
        boolean isActive = true; // default
        if (mkData.containsKey("status") && mkData.get("status") != null) {
            String status = mkData.get("status").toString();
            isActive = !"Nonaktif".equalsIgnoreCase(status);
        }
        
        // Simpan ke database
        MataKuliah mataKuliah = new MataKuliah();
        mataKuliah.setKodeMK(kode);
        mataKuliah.setNama(nama);
        mataKuliah.setSks(sks);
        mataKuliah.setActive(isActive);
        
        // Inisialisasi collections untuk menghindari null
        mataKuliah.setTugasList(new HashSet<>());
        MataKuliah savedMk = mataKuliahRepository.save(mataKuliah);
        mataKuliah.setMahasiswaList(new HashSet<>());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Mata kuliah " + nama + " berhasil ditambahkan");
        
        // Data yang disimpan
        Map<String, Object> savedData = new HashMap<>();
        savedData.put("id", savedMk.getKodeMK());
        savedData.put("kode", savedMk.getKodeMK());
        savedData.put("nama", savedMk.getNama());
        savedData.put("sks", savedMk.getSks());
        savedData.put("semester", "-");
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
 * API: Hapus mata kuliah
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
        
        // Hapus mata kuliah dari database
        mataKuliahRepository.delete(mataKuliah);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Mata kuliah " + namaMk + " berhasil dihapus");
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        return buildErrorResponse("Gagal menghapus mata kuliah: " + e.getMessage(), 
                                 HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
}