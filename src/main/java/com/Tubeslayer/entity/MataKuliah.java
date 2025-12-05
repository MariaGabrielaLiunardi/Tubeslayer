package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set; 

@Entity
@Table(name = "mata_kuliah")
@Data
public class MataKuliah {

    @Id
    @Column(name = "kode_mk", length = 15)
    private String kodeMK;

    @Column(length = 50, nullable = false)
    private String nama;

    private int sks;
    private boolean isActive = true;

    @OneToMany(mappedBy = "mataKuliah", fetch = FetchType.LAZY) // Tambah LAZY, ganti List ke Set
    private Set<TugasBesar> tugasList;

    @OneToMany(mappedBy = "mataKuliah", fetch = FetchType.LAZY) // Tambah LAZY, ganti List ke Set
    private Set<MataKuliahDosen> dosenList;

    @OneToMany(mappedBy = "mataKuliah", fetch = FetchType.LAZY) // Tambah LAZY, ganti List ke Set
    private Set<MataKuliahMahasiswa> mahasiswaList;
}

