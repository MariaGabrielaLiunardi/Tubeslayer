package com.Tubeslayer.entity.id;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class MataKuliahDosenId implements Serializable {
    private String userId;      // must match User’s PK type
    private String kodeMk;    // must match MataKuliah’s PK type
}
