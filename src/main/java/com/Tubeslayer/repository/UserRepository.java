package com.Tubeslayer.repository;

import com.Tubeslayer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    
    // Method untuk search dosen by nama (case-insensitive)
    List<User> findByRoleAndNamaContainingIgnoreCase(String role, String nama);
    
    // Opsional: Cek email sudah terdaftar
    boolean existsByEmail(String email);
    
    // Opsional: Cari by email
    Optional<User> findByEmail(String email);

    long countByRoleAndIsActive(String role, boolean isActive);
    
    /**
     * @param role1 role 1 = Mahasiswa
     * @param nama  nama
     * @param role2 role 2 = Mahasiswa)
     * @param idUser ID user
     * @return List User yang match
     */
    List<User> findByRoleAndNamaContainingIgnoreCaseOrRoleAndIdUserContaining(
        String role1, String nama, 
        String role2, String idUser
    );
    
    /**
     * @param role role user antara Dosen/Mahasiswa
     * @return List User
     */
    List<User> findByRole(String role);
    
    /**
     * @param role role user
     * @param isActive status aktif
     * @return List User
     */
    List<User> findByRoleAndIsActive(String role, boolean isActive);

}
