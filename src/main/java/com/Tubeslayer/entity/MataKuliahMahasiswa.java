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

    @MapsId("idUser") // merujuk ke field idUser di MataKuliahMahasiswaId
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @MapsId("kodeMk") // merujuk ke field kodeMk di MataKuliahMahasiswaId
    @ManyToOne
    @JoinColumn(name = "kode_mk")
    private MataKuliah mataKuliah;

    @Column(length = 3)
    private String kelas;

    private int semester;

    @Column(name = "tahun_akademik", length = 10) 
    private String tahunAkademik;

    @Column(name = "is_active") 
    private boolean isActive = true;
}
