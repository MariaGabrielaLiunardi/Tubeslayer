package com.Tubeslayer.entity;

import com.Tubeslayer.entity.id.MataKuliahMahasiswaId; 
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mata_kuliah_mahasiswa")
@Data
@IdClass(MataKuliahMahasiswaId.class)
public class MataKuliahMahasiswa {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_user") 
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "kode_mk") 
    private MataKuliah mataKuliah;

    @Column(length = 3)
    private String kelas;

    private int semester;

    @Column(name = "tahun_akademik", length = 4) 
    private String tahunAkademik;

    @Column(name = "is_active") 
    private boolean isActive = true;
}