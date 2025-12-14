package com.Tubeslayer.repository;

import com.Tubeslayer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    List<User> findByRoleAndNamaContainingIgnoreCase(String role, String nama);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByEmail(String email);
    List<User> findByRoleAndIsActiveTrue(String role);
    List<User> findByRoleAndIsActiveFalse(String role);

    long countByRoleAndIsActive(String role, boolean isActive);
    
    List<User> findByRoleAndNamaContainingIgnoreCaseOrRoleAndIdUserContaining(
        String role1, String nama, 
        String role2, String idUser
    );
    
    List<User> findByRole(String role);
    
    List<User> findByRoleAndIsActive(String role, boolean isActive);

}