package com.Tubeslayer.repository;

import com.Tubeslayer.entity.TugasBesarKelompok;
import com.Tubeslayer.entity.id.TugasBesarKelompokId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TugasBesarKelompokRepository extends JpaRepository<TugasBesarKelompok, TugasBesarKelompokId> {
    
    List<TugasBesarKelompok> findByIdTugas(Integer idTugas);
    
    List<TugasBesarKelompok> findByIdKelompok(Integer idKelompok);
    
    @Query("SELECT tbk.idKelompok, k.namaKelompok, tbk.idTugas, " +
           "COALESCE((SELECT MAX(n.nilaiKelompok) FROM Nilai n WHERE n.tugas.idTugas = tbk.idTugas " +
           "AND n.user.idUser IN (SELECT uk.user.idUser FROM UserKelompok uk WHERE uk.kelompok.idKelompok = tbk.idKelompok)), 0) " +
           "FROM TugasBesarKelompok tbk " +
           "JOIN Kelompok k ON tbk.idKelompok = k.idKelompok " +
           "WHERE tbk.idTugas = :idTugas " +
           "ORDER BY k.namaKelompok")
    List<Object[]> findGrupesWithNilaiByTugas(@Param("idTugas") Integer idTugas);
}