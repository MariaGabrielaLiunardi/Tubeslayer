package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List; 

@Entity
@Table(name = "RubrikNilai")
@Data
public class RubrikNilai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRubrik;

    @OneToOne(mappedBy = "rubrik")
    private TugasBesar tugasBesar;

    @OneToMany(mappedBy = "rubrik")
    private List<KomponenNilai> komponenList;
}
