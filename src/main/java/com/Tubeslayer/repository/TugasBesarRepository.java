package com.Tubeslayer.repository;

import com.Tubeslayer.entity.TugasBesar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TugasBesarRepository extends JpaRepository<TugasBesar, Integer> {

    // hitung jumlah tugas besar aktif untuk dosen tertentu
    int countByIdUserAndStatusAndIsActive(String idUser, String status, boolean isActive);
}
