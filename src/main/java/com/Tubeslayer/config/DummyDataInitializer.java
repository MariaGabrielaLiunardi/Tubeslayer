package com.Tubeslayer.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DummyDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DummyDataInitializer.class);

    @Bean
    public ApplicationRunner initializeMataKuliahDosen(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {

                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM mata_kuliah_dosen WHERE tahun_akademik = '2025/2026'",
                    Integer.class);
                
                if (count > 0) {
                    logger.info("Mata kuliah dosen data already initialized. Total: {}", count);
                    return;
                }
                
                logger.info("Initializing mata_kuliah_dosen data...");
                
                jdbcTemplate.update(
                    "INSERT INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                    "20250101", "AIF23001", "A", 1, "2025/2026", 1);
                
                jdbcTemplate.update(
                    "INSERT INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                    "20250101", "AIF23002", "A", 1, "2025/2026", 1);
                
                jdbcTemplate.update(
                    "INSERT INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                    "20250102", "AIF23003", "A", 1, "2025/2026", 1);
                
                jdbcTemplate.update(
                    "INSERT INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                    "20250102", "AIF23004", "B", 1, "2025/2026", 1);
                
                jdbcTemplate.update(
                    "INSERT INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES (?, ?, ?, ?, ?, ?)",
                    "20250103", "AIF23005", "A", 1, "2025/2026", 1);
                
                logger.info("✓ Mata kuliah dosen data initialized successfully!");
                
            } catch (Exception e) {
                logger.error("Error initializing mata_kuliah_dosen data: {}", e.getMessage(), e);
            }
        };
    }
}