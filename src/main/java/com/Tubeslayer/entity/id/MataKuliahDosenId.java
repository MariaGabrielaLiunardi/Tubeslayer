package com.Tubeslayer.entity.id;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class MataKuliahDosenId implements Serializable {
    private String idUser;      
    private String kodeMk;   
}
