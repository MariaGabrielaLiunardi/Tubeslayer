package com.Tubeslayer.repository;

import com.Tubeslayer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // WAJIB: Method untuk find by role
    List<User> findByRole(String role);
    
    // WAJIB: Method untuk search dosen by nama (case-insensitive)
    List<User> findByRoleAndNamaContainingIgnoreCase(String role, String nama);
    
    // Opsional: Cek email sudah terdaftar
    boolean existsByEmail(String email);
    
    // Opsional: Cari by email
    Optional<User> findByEmail(String email);
}