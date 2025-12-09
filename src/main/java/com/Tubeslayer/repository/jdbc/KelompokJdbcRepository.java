package com.Tubeslayer.repository.jdbc;

import com.Tubeslayer.dto.MahasiswaSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class KelompokJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static class MahasiswaSearchRowMapper implements RowMapper<MahasiswaSearchDTO> {
        @Override
        public MahasiswaSearchDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            MahasiswaSearchDTO dto = new MahasiswaSearchDTO();
            dto.setIdUser(rs.getString("id_user"));
            dto.setNama(rs.getString("nama"));
            dto.setNpm(rs.getString("id_user")); 
            dto.setKelas(rs.getString("kelas"));
            return dto;
        }
    }

    public List<MahasiswaSearchDTO> searchAvailableMahasiswa(Integer idTugas, String keyword) {
        String sql = 
            "SELECT DISTINCT " +
            "    u.id_user, " +
            "    u.nama, " +
            "    mkm.kelas " +
            "FROM user_table u " +
            "INNER JOIN mata_kuliah_mahasiswa mkm ON u.id_user = mkm.id_user " +
            "INNER JOIN mata_kuliah mk ON mkm.kode_mk = mk.kode_mk " +
            "INNER JOIN tugas_besar tb ON tb.kode_mk = mk.kode_mk " +
            "WHERE tb.id_tugas = ? " +
            "  AND LOWER(u.nama) LIKE LOWER(?) " +
            "  AND mkm.is_active = 1 " +
            "  AND u.id_user NOT IN ( " +
            "      SELECT uk.id_user " +
            "      FROM user_kelompok uk " +
            "      INNER JOIN tugas_besar_kelompok tbk ON uk.id_kelompok = tbk.id_kelompok " +
            "      WHERE tbk.id_tugas = ? " +
            "        AND uk.is_active = 1 " +
            "  ) " +
            "ORDER BY u.nama ASC";

        return jdbcTemplate.query(
            sql, 
            new MahasiswaSearchRowMapper(),
            idTugas,
            "%" + keyword + "%",
            idTugas
        );
    }

    /**
     * Get semua mahasiswa yang tersedia untuk tugas tertentu
     */
    public List<MahasiswaSearchDTO> getAllAvailableMahasiswa(Integer idTugas) {
        String sql = 
            "SELECT DISTINCT " +
            "    u.id_user, " +
            "    u.nama, " +
            "    mkm.kelas " +
            "FROM user_table u " +
            "INNER JOIN mata_kuliah_mahasiswa mkm ON u.id_user = mkm.id_user " +
            "INNER JOIN mata_kuliah mk ON mkm.kode_mk = mk.kode_mk " +
            "INNER JOIN tugas_besar tb ON tb.kode_mk = mk.kode_mk " +
            "WHERE tb.id_tugas = ? " +
            "  AND mkm.is_active = 1 " +
            "  AND u.id_user NOT IN ( " +
            "      SELECT uk.id_user " +
            "      FROM user_kelompok uk " +
            "      INNER JOIN tugas_besar_kelompok tbk ON uk.id_kelompok = tbk.id_kelompok " +
            "      WHERE tbk.id_tugas = ? " +
            "        AND uk.is_active = 1 " +
            "  ) " +
            "ORDER BY u.nama ASC";

        return jdbcTemplate.query(
            sql,
            new MahasiswaSearchRowMapper(),
            idTugas,
            idTugas
        );
    }

    /**
     * Get anggota kelompok untuk user tertentu pada tugas tertentu
     */
    public List<AnggotaKelompokDTO> getAnggotaKelompok(Integer idTugas, String idUser) {
        String sql = 
            "SELECT " +
            "    u.id_user, " +
            "    u.nama, " +
            "    uk.role " +
            "FROM user_kelompok uk " +
            "INNER JOIN user_table u ON uk.id_user = u.id_user " +
            "INNER JOIN tugas_besar_kelompok tbk ON uk.id_kelompok = tbk.id_kelompok " +
            "WHERE tbk.id_tugas = ? " +
            "  AND uk.is_active = 1 " +
            "  AND uk.id_kelompok = ( " +
            "      SELECT uk2.id_kelompok " +
            "      FROM user_kelompok uk2 " +
            "      INNER JOIN tugas_besar_kelompok tbk2 ON uk2.id_kelompok = tbk2.id_kelompok " +
            "      WHERE uk2.id_user = ? " +
            "        AND tbk2.id_tugas = ? " +
            "        AND uk2.is_active = 1 " +
            "      LIMIT 1 " +
            "  ) " +
            "ORDER BY " +
            "  CASE WHEN uk.role = 'leader' THEN 0 ELSE 1 END, " +
            "  u.nama ASC";

        return jdbcTemplate.query(sql, new AnggotaKelompokRowMapper(), idTugas, idUser, idTugas);
    }

    private static class AnggotaKelompokRowMapper implements RowMapper<AnggotaKelompokDTO> {
        @Override
        public AnggotaKelompokDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AnggotaKelompokDTO dto = new AnggotaKelompokDTO();
            dto.setIdUser(rs.getString("id_user"));
            dto.setNama(rs.getString("nama"));
            dto.setRole(rs.getString("role"));
            return dto;
        }
    }

    /**
     * jika user = leader
     */
    public boolean isLeader(Integer idTugas, String idUser) {
        String sql = 
            "SELECT COUNT(*) " +
            "FROM user_kelompok uk " +
            "INNER JOIN tugas_besar_kelompok tbk ON uk.id_kelompok = tbk.id_kelompok " +
            "WHERE tbk.id_tugas = ? " +
            "  AND uk.id_user = ? " +
            "  AND uk.role = 'leader' " +
            "  AND uk.is_active = 1";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idTugas, idUser);
        return count != null && count > 0;
    }

    /**
     * Get ID kelompok user untuk tugas tertentu
     */
    public Integer getKelompokIdByUser(Integer idTugas, String idUser) {
        String sql = 
            "SELECT uk.id_kelompok " +
            "FROM user_kelompok uk " +
            "INNER JOIN tugas_besar_kelompok tbk ON uk.id_kelompok = tbk.id_kelompok " +
            "WHERE tbk.id_tugas = ? " +
            "  AND uk.id_user = ? " +
            "  AND uk.is_active = 1 " +
            "LIMIT 1";

        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, idTugas, idUser);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Hitung jumlah anggota dalam kelompok
     */
    public int countAnggotaKelompok(Integer idKelompok) {
        String sql = 
            "SELECT COUNT(*) " +
            "FROM user_kelompok " +
            "WHERE id_kelompok = ? AND is_active = 1";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idKelompok);
        return count != null ? count : 0;
    }

    /**
     * Tambah anggota ke kelompok
     */
    public int tambahAnggota(String idUser, Integer idKelompok, String role) {
        String sql = 
            "INSERT INTO user_kelompok (id_user, id_kelompok, role, is_active) " +
            "VALUES (?, ?, ?, 1)";

        return jdbcTemplate.update(sql, idUser, idKelompok, role);
    }

    /**
     * Hapus anggota dari kelompok (soft delete)
     */
    public int hapusAnggota(String idUser, Integer idKelompok) {
        String sql = 
            "UPDATE user_kelompok " +
            "SET is_active = 0 " +
            "WHERE id_user = ? AND id_kelompok = ?";

        return jdbcTemplate.update(sql, idUser, idKelompok);
    }

    /**
     * Check apakah user sudah tergabung dalam kelompok untuk tugas tertentu
     */
    public boolean isUserInKelompok(Integer idTugas, String idUser) {
        String sql = 
            "SELECT COUNT(*) " +
            "FROM user_kelompok uk " +
            "INNER JOIN tugas_besar_kelompok tbk ON uk.id_kelompok = tbk.id_kelompok " +
            "WHERE tbk.id_tugas = ? " +
            "  AND uk.id_user = ? " +
            "  AND uk.is_active = 1";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idTugas, idUser);
        return count != null && count > 0;
    }

    /**
     * Get max anggota dari tugas besar
     */
    public Integer getMaxAnggota(Integer idTugas) {
        String sql = "SELECT max_anggota FROM tugas_besar WHERE id_tugas = ?";
        
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, idTugas);
        } catch (Exception e) {
            return 5; // default value
        }
    }
    /**
     * Get mode kelompok dari tugas besar
     */
    public String getModeKelompok(Integer idTugas) {
        String sql = "SELECT mode_kel FROM tugas_besar WHERE id_tugas = ?";
        
        try {
            return jdbcTemplate.queryForObject(sql, String.class, idTugas);
        } catch (Exception e) {
            return "Mahasiswa"; // default value
        }
    }

    /**
     * Get nama kelompok untuk user tertentu pada tugas tertentu
     */
    public String getNamaKelompok(Integer idTugas, String idUser) {
        String sql = 
            "SELECT k.nama_kelompok " +
            "FROM kelompok k " +
            "INNER JOIN user_kelompok uk ON k.id_kelompok = uk.id_kelompok " +
            "INNER JOIN tugas_besar_kelompok tbk ON k.id_kelompok = tbk.id_kelompok " +
            "WHERE tbk.id_tugas = ? " +
            "  AND uk.id_user = ? " +
            "  AND uk.is_active = 1 " +
            "LIMIT 1";
        
        try {
            return jdbcTemplate.queryForObject(sql, String.class, idTugas, idUser);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * DTO class untuk anggota kelompok
     */
    public static class AnggotaKelompokDTO {
        private String idUser;
        private String nama;
        private String role;

        // Getters and Setters
        public String getIdUser() { return idUser; }
        public void setIdUser(String idUser) { this.idUser = idUser; }
        
        public String getNama() { return nama; }
        public void setNama(String nama) { this.nama = nama; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}