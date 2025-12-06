package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "mata_kuliah")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MataKuliah {

    @Id
    @Column(name = "kode_mk", length = 15)
    private String kodeMK;

    @Column(length = 50, nullable = false)
    private String nama;

    private int sks;
    private boolean isActive = true;

    @OneToMany(mappedBy = "mataKuliah", fetch = FetchType.LAZY)
    private Set<TugasBesar> tugasList;

    @OneToMany(mappedBy = "mataKuliah", fetch = FetchType.LAZY)
    private Set<MataKuliahDosen> dosenList;

    @OneToMany(mappedBy = "mataKuliah", fetch = FetchType.LAZY)
    private Set<MataKuliahMahasiswa> mahasiswaList;

    @Override
    public int hashCode() {
        return kodeMK != null ? kodeMK.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MataKuliah)) return false;
        MataKuliah mk = (MataKuliah) o;
        return kodeMK != null && kodeMK.equals(mk.kodeMK);
    }
}
