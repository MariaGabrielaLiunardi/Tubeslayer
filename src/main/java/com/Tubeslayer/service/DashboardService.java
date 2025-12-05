package com.Tubeslayer.service;

import org.springframework.stereotype.Service; 
// ⬅ supaya class dikenali sebagai service oleh Spring

import com.Tubeslayer.repository.MataKuliahDosenRepository; 
// ⬅ repository untuk hitung jumlah MK aktif

import com.Tubeslayer.repository.TugasBesarRepository; 
// ⬅ repository untuk hitung jumlah tugas besar aktif

// kalau kamu butuh entity (misalnya User), bisa tambahkan:
// import com.Tubeslayer.entity.User;

@Service
public class DashboardService {

    private final MataKuliahDosenRepository mkRepo;
    private final TugasBesarRepository tbRepo;

    public DashboardService(MataKuliahDosenRepository mkRepo,
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
