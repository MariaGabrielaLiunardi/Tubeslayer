package com.Tubeslayer.dto;

import lombok.Data;

@Data
public class TugasBesarRequest {
    private String judulTugas;
    private String deadline;
    private String deskripsi;
    private String modeKel;

    public void setModeKel(String modeKel) { 
        this.modeKel = modeKel;
    }

}