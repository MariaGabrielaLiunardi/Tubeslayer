package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List; 

@Entity
@Table(name = "mata_kuliah")
@Data
public class MataKuliah {

    @Id
    @Column(length = 15)
    private String kodeMK;

    @Column(length = 50, nullable = false)
    private String nama;

    private int sks;
    private boolean isActive = true;

    @OneToMany(mappedBy = "mataKuliah")
    private List<TugasBesar> tugasList;

    @OneToMany(mappedBy = "mataKuliah")
    private List<MataKuliahDosen> dosenList;

    @OneToMany(mappedBy = "mataKuliah")
    private List<MataKuliahMahasiswa> mahasiswaList;
}

