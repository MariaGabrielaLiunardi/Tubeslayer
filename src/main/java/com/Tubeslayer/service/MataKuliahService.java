package com.Tubeslayer.service;

import com.Tubeslayer.entity.MataKuliah;
import com.Tubeslayer.entity.MataKuliahDosen;
import com.Tubeslayer.repository.MataKuliahDosenRepository;
import com.Tubeslayer.repository.MataKuliahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MataKuliahService {

    @Autowired
    private MataKuliahRepository mataKuliahRepository;

    @Autowired
    private MataKuliahDosenRepository mataKuliahDosenRepository;

    public List<MataKuliah> getActiveByMahasiswaAndTahunAkademik(String idMahasiswa, String tahunAkademik) {
        return mataKuliahRepository.findActiveByMahasiswaAndTahunAkademik(
                idMahasiswa, tahunAkademik, PageRequest.of(0, 4)
        );
    }

    public List<MataKuliahDosen> getTop4ActiveByUserAndTahunAkademik(String idUser, String tahunAkademik) {
        return mataKuliahDosenRepository.findActiveByUserAndTahunAkademik(
                idUser, tahunAkademik, PageRequest.of(0, 4)
        );
    }

    public List<MataKuliah> getMataKuliahNonAktif() {
        return mataKuliahRepository.findByIsActive(false);
    }

    public List<MataKuliah> getAll() {
        return mataKuliahRepository.findAll();
    }

    public MataKuliah save(MataKuliah mk) {
        return mataKuliahRepository.save(mk);
    }

    public void deleteByNama(String nama) {
        MataKuliah mk = mataKuliahRepository.findByNama(nama);
        if (mk != null) {
            mataKuliahRepository.delete(mk);
        }
    }

    public MataKuliah findByKodeMK(String kodeMK) {
        return mataKuliahRepository.findById(kodeMK).orElse(null);
    }
}