package com.Tubeslayer.entity.id;

import lombok.Data;
import java.io.Serializable; 
import jakarta.persistence.Embeddable;

@Embeddable
@Data
public class MataKuliahMahasiswaId implements Serializable {
    private String idUser;
    private String kodeMK;
}

