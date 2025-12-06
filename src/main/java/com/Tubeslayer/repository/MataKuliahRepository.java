package com.Tubeslayer.repository;

import com.Tubeslayer.entity.MataKuliah; // Import Entitas MataKuliah
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List; 

@Repository
// Warisi JpaRepository dengan Entitas MataKuliah dan tipe Primary Key (String)
public interface MataKuliahRepository extends JpaRepository<MataKuliah, String> {
    
    // Method untuk menghitung semua Mata Kuliah yang isActive = true
    long countByIsActive(boolean isActive); 
   // Ambil semua mata kuliah yang aktif
    List<MataKuliah> findByIsActiveTrue();
    // Ambil MK aktif berdasarkan mahasiswa dan semester
    @Query("""
    SELECT mk 
    FROM MataKuliah mk 
    JOIN mk.mahasiswaList mkm 
    WHERE mkm.user.idUser = :idMahasiswa 
      AND mk.isActive = true 
      AND mkm.isActive = true 
      AND mkm.tahunAkademik = :tahunAkademik
    """)
    List<MataKuliah> findActiveByMahasiswaAndTahunAkademik(@Param("idMahasiswa") String idMahasiswa,
                                                          @Param("tahunAkademik") String tahunAkademik,
                                                          Pageable pageable);
}