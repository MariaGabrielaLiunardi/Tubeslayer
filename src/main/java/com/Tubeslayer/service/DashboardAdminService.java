package com.Tubeslayer.service;

import com.Tubeslayer.repository.MataKuliahRepository; 
import com.Tubeslayer.repository.TugasBesarRepository;
import com.Tubeslayer.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class DashboardAdminService {

    private final MataKuliahRepository mkRepo;
    private final TugasBesarRepository tbRepo;
    private final UserRepository userRepo; 

    public DashboardAdminService(UserRepository userRepo, MataKuliahRepository mkRepo, TugasBesarRepository tbRepo) {
        this.userRepo = userRepo; 
        this.mkRepo = mkRepo;
        this.tbRepo = tbRepo;
    }

    public long getJumlahMkAktifUniversal() {
        // Panggil method repository yang menghitung semua MK aktif
        return mkRepo.countByIsActive(true);
    }

    public long getJumlahTbAktifUniversal() {
        // Panggil method repository yang menghitung semua TB aktif
        return tbRepo.countByIsActive(true);
    }

    public long getJumlahDosenAktif() {
        return userRepo.countByRoleAndIsActive("Dosen", true);
    }

    public long getJumlahMahasiswaAktif() {
        return userRepo.countByRoleAndIsActive("Mahasiswa", true);
    }

}