package com.Tubeslayer.dto;

import lombok.Data;

@Data
public class TugasBesarRequest {
    private String judulTugas;
    private String deadline; // <--- Diterima sebagai String (YYYY-MM-DD)
    private String deskripsi;
    
    // Tambahkan field lain yang dikirim oleh form jika ada
}