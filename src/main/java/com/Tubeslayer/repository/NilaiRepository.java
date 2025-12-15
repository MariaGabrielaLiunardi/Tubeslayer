package com.Tubeslayer.repository;

import com.Tubeslayer.entity.Nilai; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NilaiRepository extends JpaRepository<Nilai, Integer>{
    
    Optional<Nilai> findByUser_IdUserAndTugas_IdTugas(String idUser, Integer idTugas);
    
    List<Nilai> findByTugas_IdTugas(Integer idTugas);
    
    List<Nilai> findByUser_IdUser(String idUser);
    
    boolean existsByUser_IdUserAndTugas_IdTugas(String idUser, Integer idTugas);
    
    @Query("SELECT n FROM Nilai n " +
           "WHERE n.tugas.idTugas = :idTugas " +
           "AND n.user.idUser IN (SELECT uk.user.idUser FROM UserKelompok uk WHERE uk.kelompok.idKelompok = :idKelompok)")
    List<Nilai> findByTugasAndKelompok(@Param("idTugas") Integer idTugas, @Param("idKelompok") Integer idKelompok);
}
