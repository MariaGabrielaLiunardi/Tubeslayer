package com.Tubeslayer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profil")
public class ProfilController {

    @GetMapping("/profil")
    public String profilPage() {
        return "profil/profil"; // artinya templates/profil/profil.html
    }
}

