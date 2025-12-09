package com.Tubeslayer.service;

import org.springframework.stereotype.Service; 


import com.Tubeslayer.repository.MataKuliahDosenRepository; 


import com.Tubeslayer.repository.TugasBesarRepository; 



// import com.Tubeslayer.entity.User;

@Service
public class DashboardDosenService {

    private final MataKuliahDosenRepository mkRepo;
    private final TugasBesarRepository tbRepo;

    public DashboardDosenService(MataKuliahDosenRepository mkRepo,
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
