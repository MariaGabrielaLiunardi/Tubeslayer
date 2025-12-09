package com.Tubeslayer.repository;

import com.Tubeslayer.entity.TugasBesar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param;

@Repository
public interface TugasBesarRepository extends JpaRepository<TugasBesar, Integer> {

    @Query("SELECT DISTINCT t FROM TugasBesar t WHERE t.mataKuliah.kodeMK = :kodeMk AND t.isActive = :isActive")
    List<TugasBesar> findByMataKuliah_KodeMKAndIsActive(@Param("kodeMk") String kodeMk, @Param("isActive") boolean isActive);

    int countByDosenIdUserAndStatusAndIsActive(String idUser, String status, boolean isActive);

    @Query("SELECT COUNT(tb) " +
           "FROM TugasBesar tb " +
           "JOIN tb.mataKuliah mk " +
           "JOIN mk.mahasiswaList mkm " +
           "WHERE mkm.user.idUser = :idUser " +
           "AND tb.isActive = true " +
           "AND tb.deadline > CURRENT_TIMESTAMP")
    int countActiveByMahasiswa(@Param("idUser") String idUser);

    long countByIsActive(boolean isActive);

    // ==========================================================
    // METODE BARU UNTUK ARSIP DETAIL ADMIN
    // ==========================================================

    /**
     * Menghitung jumlah kelompok unik yang terdaftar pada TugasBesar tertentu.
     * Menggunakan tabel junction TugasBesarKelompok.
     */
    @Query("SELECT COUNT(DISTINCT tk.idKelompok) FROM TugasBesarKelompok tk WHERE tk.idTugas = :idTugas")
    Long getKelompokCount(@Param("idTugas") int idTugas);

    /**
     * Menghitung jumlah submission (entry Nilai) unik yang terkait dengan TugasBesar tertentu.
     * Asumsi: Setiap entry di tabel Nilai adalah sebuah submission.
     */

    @Query("SELECT COUNT(n) FROM Nilai n WHERE n.tugas.idTugas = :idTugas") // <-- KOREKSI INI
    Long getSubmissionCount(@Param("idTugas") int idTugas);
}