package com.Tubeslayer.entity.id;

import com.Tubeslayer.entity.User;
import com.Tubeslayer.entity.Kelompok;
import lombok.Data;
import java.io.Serializable;

@Data
public class UserKelompokId implements Serializable {
    private User user;
    private Kelompok kelompok;
}