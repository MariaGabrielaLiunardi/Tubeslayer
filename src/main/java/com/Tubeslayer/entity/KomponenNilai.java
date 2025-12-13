package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "komponen_nilai")
@Data
public class KomponenNilai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idKomponen;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "id_rubrik") 
    private RubrikNilai rubrik;
    
    @Column(length = 50, nullable = false)
    private String namaKomponen;

    @Column(nullable = false)
    private int bobot;

    @Column(columnDefinition = "VARCHAR(300)", nullable = true)
    private String catatan;
}
