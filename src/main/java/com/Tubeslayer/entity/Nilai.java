package com.Tubeslayer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Entity
@Table(name = "nilai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nilai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNilai;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tugas") 
    private TugasBesar tugas;

    private int nilaiPribadi;
    private int nilaiKelompok;
    
    @OneToMany(mappedBy = "nilai", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<NilaiKomponen> nilaiKomponenList;
}
