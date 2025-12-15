package com.Tubeslayer.entity;

import com.Tubeslayer.entity.id.MataKuliahDosenId;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mata_kuliah_dosen")
@Data
public class MataKuliahDosen {

    @EmbeddedId
    private MataKuliahDosenId id;

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

    @Column(length = 10)
    private String tahunAkademik;

    private boolean isActive = true;

    @Transient
    private int colorIndex;

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }
}
