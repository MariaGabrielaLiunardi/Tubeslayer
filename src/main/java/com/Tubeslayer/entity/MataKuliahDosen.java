package com.Tubeslayer.entity;

import com.Tubeslayer.entity.id.MataKuliahDosenId; 
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "MataKuliahDosen")
@Data
@IdClass(MataKuliahDosenId.class)
public class MataKuliahDosen {

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
