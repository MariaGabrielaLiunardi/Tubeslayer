package com.Tubeslayer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfilController {

    @GetMapping("/profil")
    public String profil() {
        return "profil/profil"; // karena file kamu ada di templates/profil/profil.html
    }
}
