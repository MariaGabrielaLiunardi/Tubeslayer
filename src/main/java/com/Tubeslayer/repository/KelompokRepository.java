package com.Tubeslayer.repository;

import com.Tubeslayer.entity.Kelompok; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KelompokRepository extends JpaRepository<Kelompok, Integer> {

    @Query("SELECT tk.kelompok FROM TugasBesarKelompok tk WHERE tk.idTugas = :idTugas")
    List<Kelompok> findByTugas(@Param("idTugas") Integer idTugas);
}