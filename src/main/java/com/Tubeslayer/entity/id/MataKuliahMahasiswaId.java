package com.Tubeslayer.entity.id;

import lombok.Data;
import java.io.Serializable; 

@Data
public class MataKuliahMahasiswaId implements Serializable {
    private String user;
    private String mataKuliah;
}
