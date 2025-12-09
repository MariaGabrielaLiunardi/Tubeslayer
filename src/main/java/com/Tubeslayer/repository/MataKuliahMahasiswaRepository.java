package com.Tubeslayer.repository;

import com.Tubeslayer.entity.MataKuliahMahasiswa;
import com.Tubeslayer.entity.id.MataKuliahMahasiswaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MataKuliahMahasiswaRepository extends JpaRepository<MataKuliahMahasiswa, MataKuliahMahasiswaId> {

    // Ambil daftar MK aktif berdasarkan user
    List<MataKuliahMahasiswa> findByUser_IdUserAndIsActive(String idUser, Boolean active);

    // Ambil daftar peserta berdasarkan kode MK
    List<MataKuliahMahasiswa> findByMataKuliah_KodeMKAndIsActive(String kodeMk, Boolean active);

    // Hitung jumlah MK aktif untuk mahasiswa tertentu di tahun akademik tertentu
    int countById_IdUserAndTahunAkademikAndIsActive(String idUser, String tahunAkademik, boolean isActive);

    List<MataKuliahMahasiswa> findByMataKuliah_KodeMK(String kodeMk);
}