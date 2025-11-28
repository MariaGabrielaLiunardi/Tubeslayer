package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @Column(name = "idUser", length = 30)
    private String idUser;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "nama", length = 60, nullable = false)
    private String nama;

    @Column(name = "role", length = 12, nullable = false)
    private String role;

    @Column(name = "isActive", nullable = false)
    private boolean isActive = true; // default true
}
