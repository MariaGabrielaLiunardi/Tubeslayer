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
// Hapus JOIN FETCH agar query hanya memuat entitas utama (TugasBesar)
@Query("SELECT DISTINCT t FROM TugasBesar t WHERE t.mataKuliah.kodeMK = :kodeMk AND t.isActive = :isActive")
List<TugasBesar> findByMataKuliah_KodeMKAndIsActive(@Param("kodeMk") String kodeMk, @Param("isActive") boolean isActive);

    // hitung jumlah tugas besar aktif untuk dosen tertentu
    int countByDosenIdUserAndStatusAndIsActive(String idUser, String status, boolean isActive);

    @Query("SELECT COUNT(tb) " +
           "FROM TugasBesar tb " +
           "JOIN tb.mataKuliah mk " +
           "JOIN mk.mahasiswaList mkm " +
           "WHERE mkm.user.idUser = :idUser " +
           "AND tb.isActive = true " +
           "AND tb.deadline > CURRENT_TIMESTAMP")
    int countActiveByMahasiswa(@Param("idUser") String idUser);

    // Method untuk menghitung semua Tugas Besar yang isActive = true
    long countByIsActive(boolean isActive);
}
