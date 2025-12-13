package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"rubrik", "mataKuliah", "tugasKelompok", "nilaiList"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tugas_besar")
public class TugasBesar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idTugas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User dosen;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_rubrik", unique = true)
    private RubrikNilai rubrik;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kode_mk", nullable = false)
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

    @OneToMany(mappedBy = "tugas", fetch = FetchType.LAZY)
    private Set<TugasBesarKelompok> tugasKelompok;

    @OneToMany(mappedBy = "tugas", fetch = FetchType.LAZY)
    private Set<Nilai> nilaiList;

    public void setDosen(User dosen) {
        this.dosen = dosen;
    }
}
