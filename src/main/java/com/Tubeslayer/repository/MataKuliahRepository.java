package com.Tubeslayer.repository;

import com.Tubeslayer.dto.MKArchiveDTO;
import com.Tubeslayer.entity.MataKuliah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page; 
import java.util.List; 

@Repository
public interface MataKuliahRepository extends JpaRepository<MataKuliah, String> {
    
    MataKuliah findByNama(String nama); 
    // Method untuk menghitung semua Mata Kuliah yang isActive = true
    long countByIsActive(boolean isActive); 
   // Ambil semua mata kuliah yang aktif
    List<MataKuliah> findByIsActiveTrue();
    List<MataKuliah> findByIsActiveFalse();

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
    List<MataKuliah> findByIsActive(boolean b);

@Query(value = """
    SELECT DISTINCT new com.Tubeslayer.dto.MKArchiveDTO(
        mk.kodeMK,
        mk.nama,
        mkm.tahunAkademik
    )
    FROM MataKuliah mk
    JOIN mk.mahasiswaList mkm
    WHERE mk.isActive = false
""",
countQuery = """
    SELECT COUNT(DISTINCT mk.kodeMK, mkm.tahunAkademik)
    FROM MataKuliah mk
    JOIN mk.mahasiswaList mkm
    WHERE mk.isActive = false
""")
Page<MKArchiveDTO> getArchiveMK(Pageable pageable);




}