package com.Tubeslayer.service;

import com.Tubeslayer.entity.*;
import com.Tubeslayer.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test Sederhana untuk NilaiService
 */
@ExtendWith(MockitoExtension.class)
class NilaiServiceTestSimple {

    @Mock
    private NilaiRepository nilaiRepository;
    @Mock
    private NilaiKomponenRepository nilaiKomponenRepository;
    @Mock
    private KomponenNilaiRepository komponenNilaiRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TugasBesarRepository tugasRepository;

    @InjectMocks
    private NilaiService nilaiService;

    private User mahasiswa;
    private TugasBesar tugas;
    private RubrikNilai rubrik;
    private KomponenNilai komponen;
    private Map<Integer, Integer> nilaiMap;

    @BeforeEach
    void setUp() {
        mahasiswa = new User();
        mahasiswa.setIdUser("mhs001");
        
        tugas = new TugasBesar();
        tugas.setIdTugas(1);
        
        rubrik = new RubrikNilai();
        rubrik.setIdRubrik(1);
        
        komponen = new KomponenNilai();
        komponen.setIdKomponen(1);
        komponen.setBobot(100);
        
        rubrik.setKomponenList(new HashSet<>(Arrays.asList(komponen)));
        tugas.setRubrik(rubrik);
        
        nilaiMap = new HashMap<>();
        nilaiMap.put(1, 85);
    }

    @Test
    void testSimpanNilai_Success() {
        when(userRepository.findById("mhs001")).thenReturn(Optional.of(mahasiswa));
        when(tugasRepository.findById(1)).thenReturn(Optional.of(tugas));
        when(komponenNilaiRepository.findById(1)).thenReturn(Optional.of(komponen));
        when(nilaiRepository.findByUser_IdUserAndTugas_IdTugas("mhs001", 1))
                .thenReturn(Optional.empty());
        
        Nilai nilaiSaved = new Nilai();
        nilaiSaved.setIdNilai(1);
        when(nilaiRepository.save(any(Nilai.class))).thenReturn(nilaiSaved);
        when(nilaiKomponenRepository.findByNilai_IdNilaiAndKomponen_IdKomponen(anyInt(), anyInt()))
                .thenReturn(Optional.empty());
        when(nilaiKomponenRepository.save(any(NilaiKomponen.class)))
                .thenReturn(new NilaiKomponen());

        Nilai result = nilaiService.simpanNilai("mhs001", 1, nilaiMap, false);
        assertNotNull(result);
        assertEquals(1, result.getIdNilai());
    }

    @Test
    void testSimpanNilai_NullUser() {
        assertThrows(IllegalArgumentException.class,
                () -> nilaiService.simpanNilai(null, 1, nilaiMap, false));
    }

    @Test
    void testSimpanNilai_InvalidTugas() {
        assertThrows(IllegalArgumentException.class,
                () -> nilaiService.simpanNilai("mhs001", 0, nilaiMap, false));
    }

    @Test
    void testSimpanNilai_NilaiNegative() {
        when(userRepository.findById("mhs001")).thenReturn(Optional.of(mahasiswa));
        when(tugasRepository.findById(1)).thenReturn(Optional.of(tugas));
        
        Map<Integer, Integer> invalidNilai = new HashMap<>();
        invalidNilai.put(1, -5);

        assertThrows(IllegalArgumentException.class,
                () -> nilaiService.simpanNilai("mhs001", 1, invalidNilai, false));
    }

    @Test
    void testSimpanNilai_NilaiTooHigh() {
        when(userRepository.findById("mhs001")).thenReturn(Optional.of(mahasiswa));
        when(tugasRepository.findById(1)).thenReturn(Optional.of(tugas));
        
        Map<Integer, Integer> invalidNilai = new HashMap<>();
        invalidNilai.put(1, 150);

        assertThrows(IllegalArgumentException.class,
                () -> nilaiService.simpanNilai("mhs001", 1, invalidNilai, false));
    }

    @Test
    void testGetNilaiByUserAndTugas_Found() {
        Nilai nilai = new Nilai();
        when(nilaiRepository.findByUser_IdUserAndTugas_IdTugas("mhs001", 1))
                .thenReturn(Optional.of(nilai));

        Nilai result = nilaiService.getNilaiByUserAndTugas("mhs001", 1);
        assertNotNull(result);
    }

    @Test
    void testGetNilaiByUserAndTugas_NotFound() {
        when(nilaiRepository.findByUser_IdUserAndTugas_IdTugas("mhs001", 1))
                .thenReturn(Optional.empty());

        Nilai result = nilaiService.getNilaiByUserAndTugas("mhs001", 1);
        assertNull(result);
    }

    @Test
    void testHapusNilai() {
        Nilai nilai = new Nilai();
        when(nilaiRepository.findByUser_IdUserAndTugas_IdTugas("mhs001", 1))
                .thenReturn(Optional.of(nilai));

        assertDoesNotThrow(() -> nilaiService.hapusNilai("mhs001", 1));
        verify(nilaiRepository).delete(nilai);
    }
}
