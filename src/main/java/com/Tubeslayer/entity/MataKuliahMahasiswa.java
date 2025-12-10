package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.Tubeslayer.entity.id.*; 


@Entity
@Table(name = "mata_kuliah_mahasiswa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MataKuliahMahasiswa {

    @EmbeddedId
    private MataKuliahMahasiswaId id;

    @MapsId("idUser")
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @MapsId("kodeMK")
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

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MataKuliahMahasiswa)) return false;
        MataKuliahMahasiswa mk = (MataKuliahMahasiswa) o;
        return id != null && id.equals(mk.id);
    }

    @Transient
    private int colorIndex; // untuk frontend, tidak disimpan ke DB

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }
}
