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

    @ManyToOne
    @MapsId("userId") // maps composite key field to association PK
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private User user;

    @ManyToOne
    @MapsId("kodeMk") // maps composite key field to association PK
    @JoinColumn(name = "kode_mk", referencedColumnName = "kodeMK")
    private MataKuliah mataKuliah;

    @Column(length = 3)
    private String kelas;

    private int semester;

    @Column(length = 4)
    private String tahunAkademik;

    private boolean isActive = true;
}
