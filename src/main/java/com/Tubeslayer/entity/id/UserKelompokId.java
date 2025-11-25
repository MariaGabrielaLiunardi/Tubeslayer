package com.Tubeslayer.entity.id;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserKelompokId implements Serializable {
    private String user;      // sama dengan tipe idUser di User
    private Integer kelompok; // sama dengan tipe idKelompok di Kelompok
}