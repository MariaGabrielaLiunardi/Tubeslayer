package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime; 
import java.time.LocalDate; 

@Entity
@Table(name = "komponen_nilai")
@Data
public class KomponenNilai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idKomponen;

// Relasi RubrikNilai (WAJIB LAZY)
    @ManyToOne(fetch = FetchType.LAZY) // <-- LAZY
    @JoinColumn(name = "id_rubrik") // Foreign Key ke RubrikNilai
    private RubrikNilai rubrik;
    
    @Column(length = 50, nullable = false)
    private String namaKomponen;

    private int bobot;

    @Column(length = 300)
    private String catatan;

    private LocalTime jam;
    private LocalDate tanggal;
}
