package com.Tubeslayer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Temporary debug controller untuk menginisialisasi data dummy
 */
@RestController
public class DebugController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/debug/check-data")
    public Map<String, Object> checkData() {
        try {
            // Check user
            int userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_table", Integer.class);
            
            // Check mata_kuliah_dosen
            int mkDosenCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM mata_kuliah_dosen", Integer.class);
            
            // Check mata_kuliah_dosen dengan tahun 2025/2026
            int mk2025Count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM mata_kuliah_dosen WHERE tahun_akademik = '2025/2026'", Integer.class);
            
            // Get specific dosen's matkul
            List<Map<String, Object>> dosenMK = jdbcTemplate.queryForList(
                "SELECT md.id_user, md.kode_mk, md.tahun_akademik, mk.nama FROM mata_kuliah_dosen md " +
                "JOIN mata_kuliah mk ON md.kode_mk = mk.kode_mk " +
                "WHERE md.tahun_akademik = '2025/2026' LIMIT 10");
            
            return Map.of(
                "userCount", userCount,
                "mkDosenCount", mkDosenCount,
                "mk2025Count", mk2025Count,
                "dosenMKList", dosenMK
            );
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    @GetMapping("/debug/init-dummy-data")
    public Map<String, String> initDummyData() {
        try {
            // Users
            jdbcTemplate.update(
                "INSERT IGNORE INTO user_table (id_user, email, password, nama, role, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                "20250101", "agus@unpar.ac.id", "$2a$12$jsq4xkNTSQQKF6O5f3ctsuxSuxXSEgu1ULt5ugH.xKBHTmDxllum2", "Agus Santoso", "Dosen", 1);
            
            jdbcTemplate.update(
                "INSERT IGNORE INTO user_table (id_user, email, password, nama, role, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                "20250102", "maria@unpar.ac.id", "$2a$12$jsq4xkNTSQQKF6O5f3ctsuxSuxXSEgu1ULt5ugH.xKBHTmDxllum2", "Maria Lestari", "Dosen", 1);
            
            jdbcTemplate.update(
                "INSERT IGNORE INTO user_table (id_user, email, password, nama, role, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                "20250103", "budi@unpar.ac.id", "$2a$12$jsq4xkNTSQQKF6O5f3ctsuxSuxXSEgu1ULt5ugH.xKBHTmDxllum2", "Budi Pranoto", "Dosen", 1);
            
            // Mata Kuliah
            jdbcTemplate.update(
                "INSERT IGNORE INTO mata_kuliah (kode_mk, nama, sks, is_active) VALUES (?, ?, ?, ?)",
                "AIF23001", "Algoritma dan Pemrograman", 3, 1);
            
            jdbcTemplate.update(
                "INSERT IGNORE INTO mata_kuliah (kode_mk, nama, sks, is_active) VALUES (?, ?, ?, ?)",
                "AIF23002", "Struktur Data", 3, 1);
            
            jdbcTemplate.update(
                "INSERT IGNORE INTO mata_kuliah (kode_mk, nama, sks, is_active) VALUES (?, ?, ?, ?)",
                "AIF23003", "Basis Data", 3, 1);
            
            jdbcTemplate.update(
                "INSERT IGNORE INTO mata_kuliah (kode_mk, nama, sks, is_active) VALUES (?, ?, ?, ?)",
                "AIF23004", "Pemrograman Web", 3, 1);
            
            jdbcTemplate.update(
                "INSERT IGNORE INTO mata_kuliah (kode_mk, nama, sks, is_active) VALUES (?, ?, ?, ?)",
                "AIF23005", "Pemrograman Mobile", 3, 1);
            
            // Mata Kuliah Dosen
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
            
            return Map.of("status", "Data dummy initialized successfully!");
        } catch (Exception e) {
            return Map.of("status", "Error: " + e.getMessage());
        }
    }
}
