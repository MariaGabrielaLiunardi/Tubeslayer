package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List; 

@Entity
@Table(name = "kelompok")
@Data
public class Kelompok {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idKelompok;

    @Column(length = 50, nullable = false)
    private String namaKelompok;

    @OneToMany(mappedBy = "kelompok")
    private List<TugasBesarKelompok> tugasKelompok;

    @OneToMany(mappedBy = "kelompok")
    private List<UserKelompok> userKelompok;
}
