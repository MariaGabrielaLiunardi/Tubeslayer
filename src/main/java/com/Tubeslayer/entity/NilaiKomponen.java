package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "nilai_komponen", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_nilai", "id_komponen"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NilaiKomponen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNilaiKomponen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nilai", nullable = false)
    @JsonBackReference
    private Nilai nilai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_komponen", nullable = false)
    private KomponenNilai komponen;

    @Column(nullable = false)
    private int nilaiKomponen;
}
