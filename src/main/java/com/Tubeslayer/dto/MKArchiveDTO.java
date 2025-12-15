package com.Tubeslayer.dto; 

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MKArchiveDTO {
    private String kodeMK;
    private String nama;
    private String tahunAkademik;

    public MKArchiveDTO(String kodeMK, String nama, String tahunAkademik) {
        this.kodeMK = kodeMK;
        this.nama = nama;
        this.tahunAkademik = tahunAkademik;
    }
}
