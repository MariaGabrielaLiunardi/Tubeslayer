package com.Tubeslayer.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Tubeslayer.service.CustomUserDetails;

@Controller
@RequestMapping("/profil")
public class ProfilController {

    @GetMapping
    public String profilPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("user", user);
        return "profil/profil"; 
    }
}
