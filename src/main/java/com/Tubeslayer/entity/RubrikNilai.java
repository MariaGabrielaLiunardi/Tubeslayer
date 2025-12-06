package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"tugasBesar", "komponenList"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rubrik_nilai")
public class RubrikNilai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idRubrik;

    @OneToOne(mappedBy = "rubrik", fetch = FetchType.LAZY, optional = false)
    private TugasBesar tugasBesar;

    @OneToMany(mappedBy = "rubrik", fetch = FetchType.LAZY)
    private Set<KomponenNilai> komponenList;
}
