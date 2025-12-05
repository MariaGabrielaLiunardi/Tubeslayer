package com.Tubeslayer.entity;

import com.Tubeslayer.entity.id.TugasBesarKelompokId;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tugas_besar_kelompok") // Nama tabel diselaraskan
@Data
@IdClass(TugasBesarKelompokId.class)
public class TugasBesarKelompok {

    @Id
    @Column(name = "id_kelompok")
    private Integer idKelompok;

    @Id
    @Column(name = "id_tugas")
    private Integer idTugas;

    @ManyToOne(fetch = FetchType.LAZY) // Relasi Kelompok
    @JoinColumn(
        name = "id_kelompok", 
        insertable = false, 
        updatable = false
    )
    private Kelompok kelompok;

    @ManyToOne(fetch = FetchType.LAZY) // Relasi TugasBesar (Telah dikoreksi)
    @JoinColumn(
        name = "id_tugas", // <--- HARUS ID_TUGAS (Nama kolom DB)
        insertable = false, 
        updatable = false
    )
    private TugasBesar tugas;
}