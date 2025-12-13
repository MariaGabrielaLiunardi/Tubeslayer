package com.Tubeslayer.dto;

import java.util.Map;

/**
 * DTO untuk request pemberian nilai per komponen
 * Digunakan saat dosen mengirimkan nilai untuk satu user
 */
public class PemberianNilaiPerKomponenDTO {
    
    private String idUser;
    private Integer idTugas;
    
    /**
     * Map<idKomponen, nilai>
     * Contoh: {1: 80, 2: 75, 3: 90}
     */
    private Map<Integer, Integer> nilaiPerKomponen;
    
    /**
     * Flag apakah nilai sama diterapkan untuk semua anggota kelompok
     */
    private boolean isSamaBuat;
    
    // Constructor
    public PemberianNilaiPerKomponenDTO() {}
    
    public PemberianNilaiPerKomponenDTO(String idUser, Integer idTugas, 
                                        Map<Integer, Integer> nilaiPerKomponen, 
                                        boolean isSamaBuat) {
        this.idUser = idUser;
        this.idTugas = idTugas;
        this.nilaiPerKomponen = nilaiPerKomponen;
        this.isSamaBuat = isSamaBuat;
    }
    
    // Getters and Setters
    public String getIdUser() {
        return idUser;
    }
    
    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
    
    public Integer getIdTugas() {
        return idTugas;
    }
    
    public void setIdTugas(Integer idTugas) {
        this.idTugas = idTugas;
    }
    
    public Map<Integer, Integer> getNilaiPerKomponen() {
        return nilaiPerKomponen;
    }
    
    public void setNilaiPerKomponen(Map<Integer, Integer> nilaiPerKomponen) {
        this.nilaiPerKomponen = nilaiPerKomponen;
    }
    
    public boolean isSamaBuat() {
        return isSamaBuat;
    }
    
    public void setSamaBuat(boolean samaBuat) {
        isSamaBuat = samaBuat;
    }
}
