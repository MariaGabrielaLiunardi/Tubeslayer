package com.Tubeslayer.repository;

import com.Tubeslayer.entity.MataKuliahDosen;
import com.Tubeslayer.entity.id.MataKuliahDosenId;
import com.Tubeslayer.dto.MKArchiveDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List; 

@Repository
public interface MataKuliahDosenRepository extends JpaRepository<MataKuliahDosen, MataKuliahDosenId> {
    
    List<MataKuliahDosen> findById_IdUserAndIsActive(String idUser, boolean isActive);

    List<MataKuliahDosen> findByMataKuliah_KodeMKAndIsActive(String kodeMk, boolean isActive);
    
    @Query("""
        SELECT md
        FROM MataKuliahDosen md
        WHERE md.user.idUser = :idUser AND md.mataKuliah.kodeMK = :kodeMK
    """)
    MataKuliahDosen findById_IdUserAndKodeMK(@Param("idUser") String idUser, @Param("kodeMK") String kodeMK);
    
    int countById_IdUserAndTahunAkademikAndIsActive(String idUser, String tahunAkademik, boolean isActive);

    @Query("""
        SELECT new com.Tubeslayer.dto.MKArchiveDTO(
            md.mataKuliah.kodeMK,
            md.mataKuliah.nama,
            md.tahunAkademik
        )
        FROM MataKuliahDosen md
        WHERE md.mataKuliah.isActive = false
        GROUP BY md.mataKuliah.kodeMK, md.mataKuliah.nama, md.tahunAkademik
        ORDER BY md.mataKuliah.nama ASC
    """)
    List<MKArchiveDTO> getArchiveMK();

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

    @Query("""
        SELECT mkm 
        FROM MataKuliahDosen mkm
        WHERE mkm.user.idUser = :idUser
          AND mkm.isActive = true
          AND mkm.tahunAkademik = :tahunAkademik
        ORDER BY mkm.mataKuliah.nama ASC
    """)
    List<MataKuliahDosen> findById_IdUserAndTahunAkademikAndIsActive(@Param("idUser") String idUser,
                                                                     @Param("tahunAkademik") String tahunAkademik);
}