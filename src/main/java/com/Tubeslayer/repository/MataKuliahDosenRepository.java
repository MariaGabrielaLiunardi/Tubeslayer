package com.Tubeslayer.repository;

import com.Tubeslayer.entity.MataKuliahDosen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
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
    // hitung jumlah mk aktif untuk dosen tertentu di tahun akademik tertentu
    int countById_IdUserAndTahunAkademikAndIsActive(String idUser, String tahunAkademik, boolean isActive);

    // ambil list MK aktif untuk dosen tertentu di tahun akademik tertentu
    @Query("""
        SELECT mkm 
        FROM MataKuliahDosen mkm
        WHERE mkm.user.idUser = :idUser
          AND mkm.isActive = true
          AND mkm.tahunAkademik = :tahunAkademik
        ORDER BY mkm.mataKuliah.nama ASC
    """)
    List<MataKuliahDosen> findActiveByUserAndTahunAkademik(@Param("idUser") String idUser,
                                                          @Param("tahunAkademik") String tahunAkademik,
                                                          Pageable pageable);

}

