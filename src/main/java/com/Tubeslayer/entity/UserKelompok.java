package com.Tubeslayer.entity;

import com.Tubeslayer.entity.id.UserKelompokId; 
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "UserKelompok")
@Data
@IdClass(UserKelompokId.class)
public class UserKelompok {

    @Id
    @ManyToOne
    @JoinColumn(name = "idUser")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "idKelompok")
    private Kelompok kelompok;

    @Column(length = 8, nullable = false)
    private String role;

    private boolean isActive = true;
}

