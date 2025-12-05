package com.Tubeslayer.repository;

import com.Tubeslayer.entity.MataKuliahDosen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MataKuliahDosenRepository extends JpaRepository<MataKuliahDosen, String> {
    
    List<MataKuliahDosen> findById_IdUserAndIsActive(String idUser, boolean isActive);

    /**
     * Mencari semua relasi MataKuliahDosen berdasarkan kode mata kuliah.
     * Ini digunakan untuk menemukan koordinator/pengajar MK.
     */
    List<MataKuliahDosen> findByMataKuliah_KodeMKAndIsActive(String kodeMk, boolean isActive); 
    
    // Hapus semua kode TugasBesar di repository ini
}