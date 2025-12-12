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

    // ============================
    // AKTIVITAS TERBARU (7 hari terakhir)
    // Note: Menampilkan perkiraan aktivitas berdasarkan data yang ada
    // Untuk implementasi penuh, tambahkan @CreatedDate field di entity
    // ============================ 
    public long getAktifitasMatkulTerbaru() {
        // Placeholder: Hitung total MK aktif sebagai indikator
        // TODO: Tambahkan @CreatedDate di entity MataKuliah dan gunakan query:
        // return mkRepo.countByCreatedDateAfter(LocalDate.now().minusDays(7));
        return mkRepo.countByIsActive(true);
    }

    public long getAktifitasDosenTerbaru() {
        // Placeholder: Hitung total Dosen aktif sebagai indikator
        // TODO: Tambahkan @CreatedDate di entity User dan gunakan query:
        // return userRepo.countByRoleAndCreatedDateAfter("Dosen", LocalDate.now().minusDays(7));
        return userRepo.countByRoleAndIsActive("Dosen", true);
    }

    public long getAktifitasMahasiswaTerbaru() {
        // Placeholder: Hitung total Mahasiswa aktif sebagai indikator
        // TODO: Tambahkan @CreatedDate di entity User dan gunakan query:
        // return userRepo.countByRoleAndCreatedDateAfter("Mahasiswa", LocalDate.now().minusDays(7));
        return userRepo.countByRoleAndIsActive("Mahasiswa", true);
    }

    public long getAktifitasTubesTerbaru() {
        // Placeholder: Hitung total Tugas Besar aktif sebagai indikator
        // TODO: Tambahkan @CreatedDate di entity TugasBesar dan gunakan query:
        // return tbRepo.countByCreatedDateAfter(LocalDate.now().minusDays(7));
        return tbRepo.countByIsActive(true);
    }

}