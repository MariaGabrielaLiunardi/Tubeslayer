package com.Tubeslayer.repository;

import com.Tubeslayer.entity.KomponenNilai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KomponenNilaiRepository extends JpaRepository<KomponenNilai, Integer> {
    List<KomponenNilai> findByRubrik_IdRubrik(Integer idRubrik);
}
