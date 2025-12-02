package com.Tubeslayer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DosenController {

    @GetMapping("/dosen/dashboard")
    public String dosenDashboard() {
        return "dosen/dashboard";  // templates/dosen/dashboard.html
    }
}
