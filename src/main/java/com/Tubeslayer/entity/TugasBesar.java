package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List; 
import java.time.LocalDateTime;

@Entity
@Table(name = "TugasBesar")
@Data
public class TugasBesar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTugas;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private User dosen;

    @OneToOne
    @JoinColumn(name = "idRubrik")
    private RubrikNilai rubrik;

    @ManyToOne
    @JoinColumn(name = "kodeMK", nullable = false)
    private MataKuliah mataKuliah;

    @Column(length = 50, nullable = false)
    private String judulTugas;

    @Column(length = 500, nullable = false)
    private String deskripsi;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(length = 50, nullable = false)
    private String status;

    @Column(length = 30, nullable = false)
    private String modeKel;

    private int minAnggota;
    private int maxAnggota;
    private boolean isActive = true;

    @OneToMany(mappedBy = "tugas")
    private List<TugasBesarKelompok> tugasKelompok;

    @OneToMany(mappedBy = "tugas")
    private List<Nilai> nilaiList;
}
