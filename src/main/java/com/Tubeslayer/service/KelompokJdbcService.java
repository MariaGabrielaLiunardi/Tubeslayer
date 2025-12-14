package com.Tubeslayer.service;

import com.Tubeslayer.dto.MahasiswaSearchDTO;
import com.Tubeslayer.repository.jdbc.KelompokJdbcRepository;
import com.Tubeslayer.repository.jdbc.KelompokJdbcRepository.AnggotaKelompokDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KelompokJdbcService {

    @Autowired
    private KelompokJdbcRepository kelompokJdbcRepo;

    public List<MahasiswaSearchDTO> searchMahasiswa(Integer idTugas, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Keyword tidak boleh kosong");
        }
        return kelompokJdbcRepo.searchAvailableMahasiswa(idTugas, keyword.trim());
    }

    public List<MahasiswaSearchDTO> getAllAvailableMahasiswa(Integer idTugas) {
        return kelompokJdbcRepo.getAllAvailableMahasiswa(idTugas);
    }

    public List<AnggotaKelompokDTO> getAnggotaKelompok(Integer idTugas, String idUser) {
        return kelompokJdbcRepo.getAnggotaKelompok(idTugas, idUser);
    }

    public boolean isLeader(Integer idTugas, String idUser) {
        return kelompokJdbcRepo.isLeader(idTugas, idUser);
    }

    public String getModeKelompok(Integer idTugas) {
        return kelompokJdbcRepo.getModeKelompok(idTugas);
    }
    
    public boolean canManageAnggota(Integer idTugas, String idUser) {
        String modeKel = getModeKelompok(idTugas);
        
        // Jika mode kelompok diatur oleh Dosen, mahasiswa tidak bisa kelola
        if ("Dosen".equalsIgnoreCase(modeKel)) {
            return false;
        }
        
        // Jika mode kelompok diatur oleh Mahasiswa hanya ketua yang bisa kelola anggota
        if ("Mahasiswa".equalsIgnoreCase(modeKel)) {
            return isLeader(idTugas, idUser);
        }
        
        return false;
    }

    public boolean hasKelompok(Integer idTugas, String idUser) {
        Integer idKelompok = kelompokJdbcRepo.getKelompokIdByUser(idTugas, idUser);
        return idKelompok != null;
    }

    @Transactional
    public void tambahAnggota(Integer idTugas, String idLeader, String idAnggotaBaru) {
        // cek apakah ketua kelompok
        if (!isLeader(idTugas, idLeader)) {
            throw new RuntimeException("Hanya leader yang dapat menambah anggota");
        }

        // cek anggota yang dimasukan apakah sudah ada di kelompok tsb
        if (kelompokJdbcRepo.isUserInKelompok(idTugas, idAnggotaBaru)) {
            throw new RuntimeException("Mahasiswa sudah tergabung dalam kelompok");
        }

        // ambil id kelompok
        Integer idKelompok = kelompokJdbcRepo.getKelompokIdByUser(idTugas, idLeader);
        if (idKelompok == null) {
            throw new RuntimeException("Kelompok tidak ditemukan");
        }

        // cek apakah anggota sudah max / melebihi batas
        int currentCount = kelompokJdbcRepo.countAnggotaKelompok(idKelompok);
        Integer maxAnggota = kelompokJdbcRepo.getMaxAnggota(idTugas);
        
        if (currentCount >= maxAnggota) {
            throw new RuntimeException("Kelompok sudah penuh (maksimal " + maxAnggota + " anggota)");
        }

        // add anggota baru dan masukan role member ke orangb tersebut
        int result = kelompokJdbcRepo.tambahAnggota(idAnggotaBaru, idKelompok, "member");
        
        if (result <= 0) {
            throw new RuntimeException("Gagal menambahkan anggota");
        }
    }

   
    @Transactional
    public void hapusAnggota(Integer idTugas, String idLeader, String idAnggotaHapus) {
        // cek apakah ketua kelompok
        if (!isLeader(idTugas, idLeader)) {
            throw new RuntimeException("Hanya leader yang dapat menghapus anggota");
        }

        // kalau dia leader tidak bisa hapus anggota 
        if (idLeader.equals(idAnggotaHapus)) {
            throw new RuntimeException("Leader tidak dapat menghapus dirinya sendiri");
        }

        // ambil id kelompok
        Integer idKelompok = kelompokJdbcRepo.getKelompokIdByUser(idTugas, idLeader);
        if (idKelompok == null) {
            throw new RuntimeException("Kelompok tidak ditemukan");
        }

        // cek apakah anggota benar2 ada di kelompok
        if (!kelompokJdbcRepo.isUserInKelompok(idTugas, idAnggotaHapus)) {
            throw new RuntimeException("Anggota tidak ditemukan dalam kelompok");
        }

        // hapus anggota 
        int result = kelompokJdbcRepo.hapusAnggota(idAnggotaHapus, idKelompok);
        
        if (result <= 0) {
            throw new RuntimeException("Gagal menghapus anggota");
        }
    }

    public int countAnggota(Integer idTugas, String idUser) {
        Integer idKelompok = kelompokJdbcRepo.getKelompokIdByUser(idTugas, idUser);
        if (idKelompok == null) {
            return 0;
        }
        return kelompokJdbcRepo.countAnggotaKelompok(idKelompok);
    }
    public int getMaxAnggota(Integer idTugas) {
        return kelompokJdbcRepo.getMaxAnggota(idTugas);
    }
    
    public String getNamaKelompok(Integer idTugas, String idUser) {
        return kelompokJdbcRepo.getNamaKelompok(idTugas, idUser);
    }

    public Integer getIdKelompok(Integer idTugas, String idUser) {
        return kelompokJdbcRepo.getKelompokIdByUser(idTugas, idUser);
    }
}