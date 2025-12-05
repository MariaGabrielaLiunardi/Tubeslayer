package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
import java.time.LocalDateTime;
import com.Tubeslayer.entity.User;
// Asumsi Entitas RubrikNilai ada
// Asumsi Entitas Nilai ada
// Asumsi Entitas TugasBesarKelompok ada


@Entity
@Table(name = "tugas_besar")
@Data
public class TugasBesar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTugas;

    // Field relasi Dosen
@ManyToOne(fetch = FetchType.LAZY) // <--- KOREKSI
    @JoinColumn(name = "id_user", nullable = false)
    private User dosen;

    // Relasi Rubrik (EAGER default -> ubah ke LAZY)
// Relasi Rubrik (Pemilik Foreign Key)
@OneToOne(fetch = FetchType.LAZY) 
@JoinColumn(name = "id_rubrik", unique = true) // <-- Tambahkan unique=true untuk validasi DB/Hibernate
private RubrikNilai rubrik;

    // Relasi Mata Kuliah (EAGER default -> ubah ke LAZY)
    @ManyToOne(fetch = FetchType.LAZY) // <--- KOREKSI
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
    private boolean isActive = true; // <-- Field isActive sudah dideklarasikan

// Koleksi One-to-Many (WAJIB LAZY dan SET)
    @OneToMany(mappedBy = "tugas", fetch = FetchType.LAZY) 
    private Set<TugasBesarKelompok> tugasKelompok; // HARUS SET

    @OneToMany(mappedBy = "tugas", fetch = FetchType.LAZY) 
    private Set<Nilai> nilaiList; // HARUS SET
    
    // Kita buat setter yang benar yang akan dipanggil oleh DosenController:
    public void setDosen(User dosen) { 
        this.dosen = dosen; 
    }
}