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

    @ManyToOne
    @JoinColumn(name = "id_tugas")
    private TugasBesar tugas;

    private int nilaiPribadi;
    private int nilaiKelompok;
}
