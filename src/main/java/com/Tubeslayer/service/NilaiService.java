package com.Tubeslayer.service;

import com.Tubeslayer.entity.*;
import com.Tubeslayer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NilaiService {

    @Autowired
    private NilaiRepository nilaiRepository;

    @Autowired
    private NilaiKomponenRepository nilaiKomponenRepository;

    @Autowired
    private KomponenNilaiRepository komponenNilaiRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TugasBesarRepository tugasRepository;

    @Transactional
    public Nilai simpanNilai(String idUser, Integer idTugas, Map<Integer, Integer> nilaiPerKomponen, boolean isSamaBuat) {
        if (idUser == null || idUser.isEmpty()) {
            throw new IllegalArgumentException("ID User tidak valid");
        }
        if (idTugas == null || idTugas <= 0) {
            throw new IllegalArgumentException("ID Tugas tidak valid");
        }
        if (nilaiPerKomponen == null || nilaiPerKomponen.isEmpty()) {
            throw new IllegalArgumentException("Nilai komponen tidak ada");
        }

        User user = userRepository.findById(idUser)
            .orElseThrow(() -> new IllegalArgumentException("User dengan ID " + idUser + " tidak ditemukan"));

        TugasBesar tugas = tugasRepository.findById(idTugas)
            .orElseThrow(() -> new IllegalArgumentException("Tugas dengan ID " + idTugas + " tidak ditemukan"));

        for (Map.Entry<Integer, Integer> entry : nilaiPerKomponen.entrySet()) {
            Integer idKomponen = entry.getKey();
            Integer nilai = entry.getValue();

            if (nilai == null || nilai < 0 || nilai > 100) {
                throw new IllegalArgumentException(
                    "Nilai untuk komponen ID " + idKomponen + " harus antara 0-100, dapat " + nilai);
            }
        }

        Nilai nilai = nilaiRepository.findByUser_IdUserAndTugas_IdTugas(idUser, idTugas)
            .orElse(new Nilai());

        nilai.setUser(user);
        nilai.setTugas(tugas);

        int nilaiKelompok = hitungNilaiKelompok(tugas.getRubrik(), nilaiPerKomponen);
        nilai.setNilaiKelompok(nilaiKelompok);

        nilai.setNilaiPribadi(nilaiKelompok);

        Nilai nilaiTersimpan = nilaiRepository.save(nilai);

        for (Map.Entry<Integer, Integer> entry : nilaiPerKomponen.entrySet()) {
            Integer idKomponen = entry.getKey();
            Integer nilaiKomponenValue = entry.getValue();

            KomponenNilai komponen = komponenNilaiRepository.findById(idKomponen)
                .orElseThrow(() -> new IllegalArgumentException("Komponen nilai dengan ID " + idKomponen + " tidak ditemukan"));

            NilaiKomponen nilaiKomponen = nilaiKomponenRepository
                .findByNilai_IdNilaiAndKomponen_IdKomponen(nilaiTersimpan.getIdNilai(), idKomponen)
                .orElse(new NilaiKomponen());

            nilaiKomponen.setNilai(nilaiTersimpan);
            nilaiKomponen.setKomponen(komponen);
            nilaiKomponen.setNilaiKomponen(nilaiKomponenValue);

            nilaiKomponenRepository.save(nilaiKomponen);
        }

        return nilaiTersimpan;
    }

    private int hitungNilaiKelompok(RubrikNilai rubrik, Map<Integer, Integer> nilaiPerKomponen) {
        if (rubrik == null || rubrik.getKomponenList() == null || rubrik.getKomponenList().isEmpty()) {
            throw new IllegalArgumentException("Rubrik atau komponen nilai tidak ditemukan");
        }

        double nilaiKelompok = 0.0;

        for (KomponenNilai komponen : rubrik.getKomponenList()) {
            Integer nilaiKomponen = nilaiPerKomponen.get(komponen.getIdKomponen());

            if (nilaiKomponen == null) {
                throw new IllegalArgumentException(
                    "Nilai untuk komponen '" + komponen.getNamaKomponen() + "' tidak ada");
            }

            nilaiKelompok += (nilaiKomponen * komponen.getBobot()) / 100.0;
        }

        return Math.round((float) nilaiKelompok);
    }

    public List<Nilai> getNilaiByTugas(Integer idTugas) {
        return nilaiRepository.findByTugas_IdTugas(idTugas);
    }

    public Nilai getNilaiByUserAndTugas(String idUser, Integer idTugas) {
        return nilaiRepository.findByUser_IdUserAndTugas_IdTugas(idUser, idTugas).orElse(null);
    }

    public List<NilaiKomponen> getNilaiKomponenByNilai(Integer idNilai) {
        return nilaiKomponenRepository.findByNilai_IdNilai(idNilai);
    }

    @Transactional
    public void hapusNilai(String idUser, Integer idTugas) {
        Nilai nilai = nilaiRepository.findByUser_IdUserAndTugas_IdTugas(idUser, idTugas)
            .orElse(null);

        if (nilai != null) {
            nilaiRepository.delete(nilai);
        }
    }

    public boolean isSemuaKomponenTerisi(Integer idTugas, Map<Integer, Integer> nilaiPerKomponen) {
        TugasBesar tugas = tugasRepository.findById(idTugas).orElse(null);
        if (tugas == null || tugas.getRubrik() == null) {
            return false;
        }

        Set<Integer> komponen = tugas.getRubrik().getKomponenList().stream()
            .map(KomponenNilai::getIdKomponen)
            .collect(Collectors.toSet());

        return komponen.equals(nilaiPerKomponen.keySet());
    }

    @Transactional
    public void terapkanNilaiSamaKelompok(Integer idKelompok, Integer idTugas, 
                                          Map<Integer, Integer> nilaiPerKomponen) {
        
    }
}
