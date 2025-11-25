package com.Tubeslayer.entity;

import com.Tubeslayer.entity.id.MataKuliahMahasiswaId; 
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "MataKuliahMahasiswa")
@Data
@IdClass(MataKuliahMahasiswaId.class)
public class MataKuliahMahasiswa {

    @Id
    @ManyToOne
    @JoinColumn(name = "idUser")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "kodeMK")
    private MataKuliah mataKuliah;

    @Column(length = 3)
    private String kelas;

    private int semester;

    @Column(length = 4)
    private String tahunAkademik;

    private boolean isActive = true;
}
