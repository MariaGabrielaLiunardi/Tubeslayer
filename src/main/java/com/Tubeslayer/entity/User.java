package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.Set;

import org.springframework.data.repository.query.Param;

@Entity
@Table(name = "user_table")
@Data
@EqualsAndHashCode(exclude = {"tugasBesarList", "mataKuliahMahasiswaList"}) 
@ToString(exclude = {"tugasBesarList", "mataKuliahMahasiswaList"})
public class User {
    @Id
    @Column(name = "id_user", length = 30)
    private String idUser;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "nama", length = 60, nullable = false)
    private String nama;

    @Column(name = "role", length = 12, nullable = false)
    private String role;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // default true


    
}
