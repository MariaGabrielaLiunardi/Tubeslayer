package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "nilai")
@Data
public class Nilai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNilai;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

// Relasi Tugas (ManyToOne)
@ManyToOne(fetch = FetchType.LAZY)
// TAMBAHKAN unique=true di sini jika satu Tugas hanya boleh memiliki satu Nilai.
@JoinColumn(name = "id_tugas", unique = true) 
private TugasBesar tugas;

    private int nilaiPribadi;
    private int nilaiKelompok;
}
