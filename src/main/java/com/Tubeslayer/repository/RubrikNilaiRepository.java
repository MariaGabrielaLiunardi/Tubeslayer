package com.Tubeslayer.repository;

import com.Tubeslayer.entity.RubrikNilai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RubrikNilaiRepository extends JpaRepository<RubrikNilai, Integer> {
}