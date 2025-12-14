package com.Tubeslayer.repository;

import com.Tubeslayer.entity.MataKuliahMahasiswa;
import com.Tubeslayer.entity.id.MataKuliahMahasiswaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MataKuliahMahasiswaRepository extends JpaRepository<MataKuliahMahasiswa, MataKuliahMahasiswaId> {

    List<MataKuliahMahasiswa> findByUser_IdUserAndIsActive(String idUser, Boolean active);

    List<MataKuliahMahasiswa> findByMataKuliah_KodeMKAndIsActive(String kodeMk, Boolean active);

    int countById_IdUserAndTahunAkademikAndIsActive(String idUser, String tahunAkademik, boolean isActive);

    List<MataKuliahMahasiswa> findByMataKuliah_KodeMK(String kodeMk);
    
    @Query("SELECT DISTINCT u.nama, u.idUser, COALESCE(n.nilaiPribadi, 0) FROM User u " +
           "JOIN MataKuliahMahasiswa mkm ON u.idUser = mkm.user.idUser " +
           "LEFT JOIN Nilai n ON u.idUser = n.user.idUser AND n.tugas.idTugas = :idTugas " +
           "WHERE mkm.isActive = true AND mkm.mataKuliah.kodeMK IN " +
           "(SELECT tb.mataKuliah.kodeMK FROM TugasBesar tb WHERE tb.idTugas = :idTugas) " +
           "ORDER BY u.nama")
    List<Object[]> findPesertaWithNilaiByTugas(@Param("idTugas") Integer idTugas);
}