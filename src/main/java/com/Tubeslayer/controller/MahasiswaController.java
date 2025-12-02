package com.Tubeslayer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MahasiswaController {

    @GetMapping("/mahasiswa/dashboard")
    public String mahasiswaDashboard() {
        return "mahasiswa/dashboard"; // templates/mahasiswa/dashboard.html
    }
}