package com.Tubeslayer.entity;

import com.Tubeslayer.entity.id.TugasBesarKelompokId;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TugasBesarKelompok")
@Data
@IdClass(TugasBesarKelompokId.class)
public class TugasBesarKelompok {

    @Id
    @Column(name = "id_kelompok")
    private Integer idKelompok;

    @Id
    @Column(name = "id_tugas")
    private Integer idTugas;

    @ManyToOne
    @JoinColumn(
        name = "id_kelompok", 
        insertable = false, 
        updatable = false
    )
    private Kelompok kelompok;

    @ManyToOne
    @JoinColumn(name = "TugasBesarId")
    private TugasBesar tugas;
}

