package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List; 

@Entity
@Table(name = "User")
@Data
public class User {

    @Id
    @Column(name = "idUser", length = 30)
    private String idUser;

    @Column(unique = true, length = 50)
    private String email;

    @Column(length = 30, nullable = false)
    private String password;

    @Column(length = 60, nullable = false)
    private String nama;

    @Column(length = 12, nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean isActive = true;

    // Relasi dengan TugasBesar (sebagai dosen)
    @OneToMany(mappedBy = "dosen")
    private List<TugasBesar> tugasList;

    // Relasi dengan UserKelompok
    @OneToMany(mappedBy = "user")
    private List<UserKelompok> userKelompok;

    // Relasi dengan Nilai
    @OneToMany(mappedBy = "user")
    private List<Nilai> nilaiList;
}
