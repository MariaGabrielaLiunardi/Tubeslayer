package com.Tubeslayer.service;

import org.springframework.stereotype.Service; 

import com.Tubeslayer.repository.MataKuliahMahasiswaRepository; 

import com.Tubeslayer.repository.TugasBesarRepository; 

@Service
public class DashboardMahasiswaService {

    private final MataKuliahMahasiswaRepository mkRepo;
    private final TugasBesarRepository tbRepo;

    public DashboardMahasiswaService(MataKuliahMahasiswaRepository mkRepo,
                            TugasBesarRepository tbRepo) {
        this.mkRepo = mkRepo;
        this.tbRepo = tbRepo;
    }

    public int getJumlahMkAktif(String idUser, String tahunAkademik) {
        return mkRepo.countById_IdUserAndTahunAkademikAndIsActive(idUser, tahunAkademik, true);
    }

    public int getJumlahTbAktif(String idUser) {
        return tbRepo.countByDosenIdUserAndStatusAndIsActive(idUser, "Open", true);
    }
}