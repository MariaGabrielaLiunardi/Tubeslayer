package com.Tubeslayer.repository;

import com.Tubeslayer.entity.TugasBesar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface TugasBesarRepository extends JpaRepository<TugasBesar, Integer> {

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
