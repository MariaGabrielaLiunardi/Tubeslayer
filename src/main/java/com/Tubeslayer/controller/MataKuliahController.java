package com.Tubeslayer.controller;

import com.Tubeslayer.entity.MataKuliah;
import com.Tubeslayer.service.MataKuliahService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin/matkul")
public class MataKuliahController {

    @Autowired
    private MataKuliahService service;

    @GetMapping("/kelola-mata-kuliah")
    public String kelolaMatkul(Model model) {

        model.addAttribute("listMatkul", service.getAll());
        return "admin/kelola-mata-kuliah";
    }

    @PostMapping("/tambah")
    @ResponseBody
    public String tambah(@RequestBody MataKuliah mk) {
        service.save(mk);
        return "OK";
    }

    @PostMapping("/hapus")
    @ResponseBody
    public String hapus(@RequestParam String nama) {
        service.deleteByNama(nama);
        return "OK";
    }
}