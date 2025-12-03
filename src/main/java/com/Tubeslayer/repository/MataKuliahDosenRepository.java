package com.Tubeslayer.repository;

import com.Tubeslayer.entity.MataKuliahDosen;
import com.Tubeslayer.entity.id.MataKuliahDosenId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MataKuliahDosenRepository extends JpaRepository<MataKuliahDosen, MataKuliahDosenId> {

    // hitung jumlah mk aktif untuk dosen tertentu di tahun akademik tertentu
    int countById_IdUserAndTahunAkademikAndIsActive(String idUser, String tahunAkademik, boolean isActive);
}
