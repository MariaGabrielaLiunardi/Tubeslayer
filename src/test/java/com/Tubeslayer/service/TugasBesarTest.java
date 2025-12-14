package com.Tubeslayer.service;

import com.Tubeslayer.entity.MataKuliah;
import com.Tubeslayer.entity.RubrikNilai;
import com.Tubeslayer.entity.TugasBesar;
import com.Tubeslayer.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test Sederhana untuk TugasBesar Entity
 */
class TugasBesarTestSimple {

    private TugasBesar tugasBesar;
    private User dosen;
    private MataKuliah mataKuliah;
    private RubrikNilai rubrik;

    @BeforeEach
    void setUp() {
        dosen = new User();
        dosen.setIdUser("dosen001");
        
        mataKuliah = new MataKuliah();
        mataKuliah.setKodeMK("IF2110");
        mataKuliah.setNama("Algoritma");
        
        rubrik = new RubrikNilai();
        rubrik.setIdRubrik(1);
        
        tugasBesar = new TugasBesar();
        tugasBesar.setDosen(dosen);
        tugasBesar.setMataKuliah(mataKuliah);
        tugasBesar.setRubrik(rubrik);
    }

    @Test
    void testCreateTugaBesar() {
        tugasBesar.setJudulTugas("Implementasi Sort");
        tugasBesar.setDeskripsi("Implementasi quick sort");
        tugasBesar.setDeadline(LocalDateTime.now().plusDays(14));
        tugasBesar.setStatus("Aktif");
        tugasBesar.setModeKel("Mahasiswa");
        tugasBesar.setMinAnggota(2);
        tugasBesar.setMaxAnggota(5);

        assertEquals("Implementasi Sort", tugasBesar.getJudulTugas());
        assertEquals("Aktif", tugasBesar.getStatus());
        assertEquals(2, tugasBesar.getMinAnggota());
        assertEquals(5, tugasBesar.getMaxAnggota());
        assertTrue(tugasBesar.isActive());
    }

    @Test
    void testRequiredFieldsNotNull() {
        tugasBesar.setJudulTugas("Tugas 1");
        tugasBesar.setDeskripsi("Deskripsi tugas");
        tugasBesar.setDeadline(LocalDateTime.now().plusDays(7));
        tugasBesar.setStatus("Aktif");
        tugasBesar.setModeKel("Mahasiswa");

        assertNotNull(tugasBesar.getJudulTugas());
        assertNotNull(tugasBesar.getDeskripsi());
        assertNotNull(tugasBesar.getDeadline());
        assertNotNull(tugasBesar.getDosen());
        assertNotNull(tugasBesar.getMataKuliah());
    }

    @Test
    void testModeKelompok_Mahasiswa() {
        tugasBesar.setModeKel("Mahasiswa");
        assertEquals("Mahasiswa", tugasBesar.getModeKel());
    }

    @Test
    void testModeKelompok_Dosen() {
        tugasBesar.setModeKel("Dosen");
        assertEquals("Dosen", tugasBesar.getModeKel());
    }

    @Test
    void testMinMaxAnggota() {
        tugasBesar.setMinAnggota(2);
        tugasBesar.setMaxAnggota(5);
        assertTrue(tugasBesar.getMinAnggota() <= tugasBesar.getMaxAnggota());
    }

    @Test
    void testStatusActive() {
        tugasBesar.setStatus("Aktif");
        tugasBesar.setActive(true);
        assertEquals("Aktif", tugasBesar.getStatus());
        assertTrue(tugasBesar.isActive());
    }

    @Test
    void testRelationWithDosen() {
        assertNotNull(tugasBesar.getDosen());
        assertEquals("dosen001", tugasBesar.getDosen().getIdUser());
    }

    @Test
    void testRelationWithMataKuliah() {
        assertNotNull(tugasBesar.getMataKuliah());
        assertEquals("IF2110", tugasBesar.getMataKuliah().getKodeMK());
    }
}
