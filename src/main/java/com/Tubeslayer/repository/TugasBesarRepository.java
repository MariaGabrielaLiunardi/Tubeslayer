 package com.Tubeslayer.repository;

import com.Tubeslayer.entity.TugasBesar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param;

@Repository
public interface TugasBesarRepository extends JpaRepository<TugasBesar, Integer> {
// Hapus JOIN FETCH agar query hanya memuat entitas utama (TugasBesar)
@Query("SELECT DISTINCT t FROM TugasBesar t WHERE t.mataKuliah.kodeMK = :kodeMk AND t.isActive = :isActive")
List<TugasBesar> findByMataKuliah_KodeMKAndIsActive(@Param("kodeMk") String kodeMk, @Param("isActive") boolean isActive);

}
