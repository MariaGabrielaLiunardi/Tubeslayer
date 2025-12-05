package com.Tubeslayer.repository;

import com.Tubeslayer.entity.MataKuliahMahasiswa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
// Catatan: Menggunakan Object sebagai tipe ID untuk fleksibilitas jika ID-nya gabungan
public interface MataKuliahMahasiswaRepository extends JpaRepository<MataKuliahMahasiswa, Object> { 
    
    // KOREKSI: Mengganti findByMahasiswa menjadi findByUser (asumsi relasi bernama 'user')
    List<MataKuliahMahasiswa> findByUser_IdUserAndIsActive(String idUser, Boolean active);
    
    // METHOD BARU UNTUK MENDAPATKAN DAFTAR PESERTA BERDASARKAN MATA KULIAH
    List<MataKuliahMahasiswa> findByMataKuliah_KodeMKAndIsActive(String kodeMk, Boolean active);
}