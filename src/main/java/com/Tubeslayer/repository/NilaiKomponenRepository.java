package com.Tubeslayer.repository;

import com.Tubeslayer.entity.NilaiKomponen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NilaiKomponenRepository extends JpaRepository<NilaiKomponen, Integer> {
    
    Optional<NilaiKomponen> findByNilai_IdNilaiAndKomponen_IdKomponen(Integer idNilai, Integer idKomponen);
    
    List<NilaiKomponen> findByNilai_IdNilai(Integer idNilai);
    
    List<NilaiKomponen> findByKomponen_IdKomponen(Integer idKomponen);
}
