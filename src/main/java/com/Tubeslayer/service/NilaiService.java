package com.Tubeslayer.service;

import com.Tubeslayer.entity.*;
import com.Tubeslayer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service untuk menangani logika pemberian nilai oleh dosen
 * Termasuk validasi, perhitungan nilai kelompok, dan penyimpanan ke database
 */
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

    /**
     * Simpan nilai untuk satu user untuk satu tugas dengan nilai per komponen
     * 
     * @param idUser ID user (mahasiswa) yang mendapat nilai
     * @param idTugas ID tugas yang dinilai
     * @param nilaiPerKomponen Map<idKomponen, nilaiKomponen>
     * @param isSamaBuat boolean apakah nilai sama untuk semua member
     * @return Nilai yang sudah disimpan
     */
    @Transactional
    public Nilai simpanNilai(String idUser, Integer idTugas, Map<Integer, Integer> nilaiPerKomponen, boolean isSamaBuat) {
        // Validasi input
        if (idUser == null || idUser.isEmpty()) {
            throw new IllegalArgumentException("ID User tidak valid");
        }
        if (idTugas == null || idTugas <= 0) {
            throw new IllegalArgumentException("ID Tugas tidak valid");
        }
        if (nilaiPerKomponen == null || nilaiPerKomponen.isEmpty()) {
            throw new IllegalArgumentException("Nilai komponen tidak ada");
        }

        // Cek user ada
        User user = userRepository.findById(idUser)
            .orElseThrow(() -> new IllegalArgumentException("User dengan ID " + idUser + " tidak ditemukan"));

        // Cek tugas ada
        TugasBesar tugas = tugasRepository.findById(idTugas)
            .orElseThrow(() -> new IllegalArgumentException("Tugas dengan ID " + idTugas + " tidak ditemukan"));

        // Validasi nilai per komponen (0-100)
        for (Map.Entry<Integer, Integer> entry : nilaiPerKomponen.entrySet()) {
            Integer idKomponen = entry.getKey();
            Integer nilai = entry.getValue();

            if (nilai == null || nilai < 0 || nilai > 100) {
                throw new IllegalArgumentException(
                    "Nilai untuk komponen ID " + idKomponen + " harus antara 0-100, dapat " + nilai);
            }
        }

        // Cari atau buat nilai baru
        Nilai nilai = nilaiRepository.findByUser_IdUserAndTugas_IdTugas(idUser, idTugas)
            .orElse(new Nilai());

        // Set user dan tugas
        nilai.setUser(user);
        nilai.setTugas(tugas);

        // Hitung nilai kelompok dari komponen
        int nilaiKelompok = hitungNilaiKelompok(tugas.getRubrik(), nilaiPerKomponen);
        nilai.setNilaiKelompok(nilaiKelompok);

        // Set nilai pribadi (sesuai dengan nilai kelompok karena data individual akan ditentukan belakangan)
        nilai.setNilaiPribadi(nilaiKelompok);

        // Simpan nilai utama
        Nilai nilaiTersimpan = nilaiRepository.save(nilai);

        // Simpan nilai per komponen
        for (Map.Entry<Integer, Integer> entry : nilaiPerKomponen.entrySet()) {
            Integer idKomponen = entry.getKey();
            Integer nilaiKomponenValue = entry.getValue();

            KomponenNilai komponen = komponenNilaiRepository.findById(idKomponen)
                .orElseThrow(() -> new IllegalArgumentException("Komponen nilai dengan ID " + idKomponen + " tidak ditemukan"));

            // Cari atau buat NilaiKomponen
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

    /**
     * Hitung nilai kelompok berdasarkan nilai komponen dan bobot
     * Rumus: Nilai Kelompok = Sum(nilai_komponen_i * bobot_i / 100)
     * 
     * @param rubrik RubrikNilai yang berisi daftar komponen dengan bobot
     * @param nilaiPerKomponen Map<idKomponen, nilaiKomponen>
     * @return Nilai kelompok yang sudah dihitung
     */
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

            // Kalkulasi: (nilai * bobot) / 100
            nilaiKelompok += (nilaiKomponen * komponen.getBobot()) / 100.0;
        }

        // Round ke integer
        return Math.round((float) nilaiKelompok);
    }

    /**
     * Ambil semua nilai untuk satu tugas
     */
    public List<Nilai> getNilaiByTugas(Integer idTugas) {
        return nilaiRepository.findByTugas_IdTugas(idTugas);
    }

    /**
     * Ambil nilai untuk satu user dan tugas
     */
    public Nilai getNilaiByUserAndTugas(String idUser, Integer idTugas) {
        return nilaiRepository.findByUser_IdUserAndTugas_IdTugas(idUser, idTugas).orElse(null);
    }

    /**
     * Ambil nilai komponen untuk satu nilai
     */
    public List<NilaiKomponen> getNilaiKomponenByNilai(Integer idNilai) {
        return nilaiKomponenRepository.findByNilai_IdNilai(idNilai);
    }

    /**
     * Hapus nilai untuk user dan tugas tertentu
     */
    @Transactional
    public void hapusNilai(String idUser, Integer idTugas) {
        Nilai nilai = nilaiRepository.findByUser_IdUserAndTugas_IdTugas(idUser, idTugas)
            .orElse(null);

        if (nilai != null) {
            nilaiRepository.delete(nilai);
        }
    }

    /**
     * Validasi apakah semua komponen sudah memiliki nilai
     */
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

    /**
     * Terapkan nilai sama untuk semua member kelompok
     */
    @Transactional
    public void terapkanNilaiSamaKelompok(Integer idKelompok, Integer idTugas, 
                                          Map<Integer, Integer> nilaiPerKomponen) {
        // Ambil semua member kelompok
        // TODO: Implementasi sesuai dengan struktur UserKelompok
    }
}
