package com.Tubeslayer.repository;

//import com.Tubeslayer.model.MataKuliah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Tubeslayer.entity.MataKuliahMahasiswa;
import com.Tubeslayer.entity.id.MataKuliahMahasiswaId;

// JpaRepository<[Nama Entitas], [Tipe Data Primary Key]>

@Repository
public interface MataKuliahMahasiswaRepository extends JpaRepository<MataKuliahMahasiswa, MataKuliahMahasiswaId> {

    // hitung jumlah MK aktif untuk mahasiswa tertentu
    int countById_IdUserAndTahunAkademikAndIsActive(String idUser, String tahunAkademik, boolean isActive);
}
