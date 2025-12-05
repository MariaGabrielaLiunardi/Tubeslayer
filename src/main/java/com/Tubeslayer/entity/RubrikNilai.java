package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set; 

@Entity
@Table(name = "rubrik_nilai")
@Data
public class RubrikNilai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRubrik;

@OneToOne(mappedBy = "rubrik", fetch = FetchType.LAZY, optional = false) // <--- Tambah optional=false (Jika wajib)
private TugasBesar tugasBesar;

    // Relasi KomponenNilai (WAJIB LAZY dan SET)
    @OneToMany(mappedBy = "rubrik", fetch = FetchType.LAZY) 
    private Set<KomponenNilai> komponenList; // <-- WAJIB SET
}
