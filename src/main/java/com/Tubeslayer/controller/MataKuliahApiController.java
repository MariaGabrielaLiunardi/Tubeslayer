package com.Tubeslayer.controller;

import com.Tubeslayer.entity.MataKuliah;
import com.Tubeslayer.repository.MataKuliahRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mata-kuliah")
public class MataKuliahApiController {

    private final MataKuliahRepository repository;

    public MataKuliahApiController(MataKuliahRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<MataKuliah> getAllActive() {
        return repository.findByIsActiveTrue();
    }

    @PostMapping
    public ResponseEntity<MataKuliah> addMataKuliah(@RequestBody MataKuliah mk) {
        MataKuliah savedMK = repository.save(mk);
        return new ResponseEntity<>(savedMK, HttpStatus.CREATED);
    }

    @DeleteMapping("/{kodeMK}")
    public ResponseEntity<String> deleteMataKuliah(@PathVariable String kodeMK) {
        Optional<MataKuliah> mk = repository.findById(kodeMK);
        if (mk.isPresent()) {
            repository.deleteById(kodeMK);
            return new ResponseEntity<>("Mata kuliah berhasil dihapus", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Mata kuliah tidak ditemukan", HttpStatus.NOT_FOUND);
        }
    }
}