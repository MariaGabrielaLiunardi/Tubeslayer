package com.Tubeslayer.dto;

import lombok.Data;

@Data
public class TugasBesarRequest {
    private String judulTugas;
    private String deadline; // <--- Diterima sebagai String (YYYY-MM-DD)
    private String deskripsi;
    private String modeKel;

    public void setModeKel(String modeKel) { 
        this.modeKel = modeKel;
    }

}