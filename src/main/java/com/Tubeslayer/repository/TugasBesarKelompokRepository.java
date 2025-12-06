package com.Tubeslayer.repository;

import com.Tubeslayer.entity.TugasBesarKelompok;
import com.Tubeslayer.entity.id.TugasBesarKelompokId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TugasBesarKelompokRepository extends JpaRepository<TugasBesarKelompok, TugasBesarKelompokId> {
    
    /**
     * @param idTugas ID tugas
     * @return List TugasBesarKelompok
     */
    List<TugasBesarKelompok> findByIdTugas(Integer idTugas);
    
    /**
     * @param idKelompok ID kelompok
     * @return List TugasBesarKelompok
     */
    List<TugasBesarKelompok> findByIdKelompok(Integer idKelompok);
}