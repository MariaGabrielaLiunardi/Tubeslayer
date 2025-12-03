package com.Tubeslayer.entity;

import com.Tubeslayer.entity.id.MataKuliahMahasiswaId; 
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mata_kuliah_mahasiswa")
@Data
public class MataKuliahMahasiswa {

    @EmbeddedId
    private MataKuliahMahasiswaId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private User user;

    @ManyToOne
    @MapsId("kodeMk")
    @JoinColumn(name = "kode_mk", referencedColumnName = "kodeMK")
    private MataKuliah mataKuliah;

    @Column(length = 3)
    private String kelas;

    private int semester;

    @Column(length = 4)
    private String tahunAkademik;

    private boolean isActive = true;
}