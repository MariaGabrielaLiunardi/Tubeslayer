package com.Tubeslayer.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data 
public class TugasDto {

    @NotBlank(message = "Nama tugas wajib diisi")
    private String nama;

    @NotNull(message = "Deadline wajib diisi")
    private LocalDate deadline; // Spring Boot konversi ke String YYYY-MM-DD

    @NotBlank(message = "Deskripsi tugas wajib diisi")
    private String deskripsi;
}