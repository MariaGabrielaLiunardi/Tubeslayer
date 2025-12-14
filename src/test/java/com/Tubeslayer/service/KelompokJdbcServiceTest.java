package com.Tubeslayer.service;

import com.Tubeslayer.repository.jdbc.KelompokJdbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test Sederhana untuk KelompokJdbcService
 */
@ExtendWith(MockitoExtension.class)
class KelompokJdbcServiceTest {

    @Mock
    private KelompokJdbcRepository kelompokJdbcRepo;

    @InjectMocks
    private KelompokJdbcService kelompokService;

    @Test
    void testSearchMahasiswa_Success() {
        String keyword = "Budi";
        kelompokService.searchMahasiswa(1, keyword);
        verify(kelompokJdbcRepo).searchAvailableMahasiswa(1, keyword.trim());
    }

    @Test
    void testSearchMahasiswa_ThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> kelompokService.searchMahasiswa(1, null));
    }

    @Test
    void testTambahAnggota_Success() {
        when(kelompokJdbcRepo.isLeader(1, "leader")).thenReturn(true);
        when(kelompokJdbcRepo.getKelompokIdByUser(1, "leader")).thenReturn(100);
        when(kelompokJdbcRepo.isUserInKelompok(1, "anggota")).thenReturn(false);
        when(kelompokJdbcRepo.countAnggotaKelompok(100)).thenReturn(2);
        when(kelompokJdbcRepo.getMaxAnggota(1)).thenReturn(5);
        when(kelompokJdbcRepo.tambahAnggota("anggota", 100, "member")).thenReturn(1);

        assertDoesNotThrow(() -> 
            kelompokService.tambahAnggota(1, "leader", "anggota"));
    }

    @Test
    void testTambahAnggota_NotLeader() {
        when(kelompokJdbcRepo.isLeader(1, "user")).thenReturn(false);
        assertThrows(RuntimeException.class, 
            () -> kelompokService.tambahAnggota(1, "user", "anggota"));
    }

    @Test
    void testHapusAnggota_Success() {
        when(kelompokJdbcRepo.isLeader(1, "leader")).thenReturn(true);
        when(kelompokJdbcRepo.getKelompokIdByUser(1, "leader")).thenReturn(100);
        when(kelompokJdbcRepo.isUserInKelompok(1, "anggota")).thenReturn(true);
        when(kelompokJdbcRepo.hapusAnggota("anggota", 100)).thenReturn(1);

        assertDoesNotThrow(() -> 
            kelompokService.hapusAnggota(1, "leader", "anggota"));
    }

    @Test
    void testCanManageAnggota_DosenMode() {
        when(kelompokJdbcRepo.getModeKelompok(1)).thenReturn("Dosen");
        assertFalse(kelompokService.canManageAnggota(1, "user"));
    }

    @Test
    void testCanManageAnggota_MahasiswaMode_Leader() {
        when(kelompokJdbcRepo.getModeKelompok(1)).thenReturn("Mahasiswa");
        when(kelompokJdbcRepo.isLeader(1, "leader")).thenReturn(true);
        assertTrue(kelompokService.canManageAnggota(1, "leader"));
    }
}
