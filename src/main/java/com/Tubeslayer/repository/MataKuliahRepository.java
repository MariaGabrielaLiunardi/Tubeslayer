package com.Tubeslayer.repository;

import com.Tubeslayer.entity.MataKuliah; // Import Entitas MataKuliah
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Warisi JpaRepository dengan Entitas MataKuliah dan tipe Primary Key (String)
public interface MataKuliahRepository extends JpaRepository<MataKuliah, String> {
    
    // Method untuk menghitung semua Mata Kuliah yang isActive = true
    long countByIsActive(boolean isActive); 
}