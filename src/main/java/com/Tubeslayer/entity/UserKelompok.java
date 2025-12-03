package com.Tubeslayer.entity;

import com.Tubeslayer.entity.id.UserKelompokId; 
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_kelompok")
@Data
@IdClass(UserKelompokId.class)
public class UserKelompok {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_kelompok")
    private Kelompok kelompok;

    @Column(length = 8, nullable = false)
    private String role;

    private boolean is_active = true;
}

