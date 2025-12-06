package com.Tubeslayer.service;

import com.Tubeslayer.repository.MataKuliahRepository; 
import com.Tubeslayer.repository.TugasBesarRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardAdminService {

    private final MataKuliahRepository mkRepo;
    private final TugasBesarRepository tbRepo;

    public DashboardAdminService(MataKuliahRepository mkRepo, TugasBesarRepository tbRepo) {
        this.mkRepo = mkRepo;
        this.tbRepo = tbRepo;
    }

    // ⭐ Ubah method: Hapus parameter idUser dan semester
    public long getJumlahMkAktifUniversal() {
        // Panggil method repository yang menghitung semua MK aktif
        return mkRepo.countByIsActive(true);
    }

    // ⭐ Ubah method: Hapus parameter idUser
    public long getJumlahTbAktifUniversal() {
        // Panggil method repository yang menghitung semua TB aktif
        return tbRepo.countByIsActive(true);
    }
}