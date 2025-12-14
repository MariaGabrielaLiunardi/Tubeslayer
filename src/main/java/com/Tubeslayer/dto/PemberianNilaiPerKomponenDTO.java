package com.Tubeslayer.dto;

import java.util.Map;

public class PemberianNilaiPerKomponenDTO {
    
    private String idUser;
    private Integer idTugas;
    
    private Map<Integer, Integer> nilaiPerKomponen;
    
    private boolean isSamaBuat;
    
    public PemberianNilaiPerKomponenDTO() {}
    
    public PemberianNilaiPerKomponenDTO(String idUser, Integer idTugas, 
                                        Map<Integer, Integer> nilaiPerKomponen, 
                                        boolean isSamaBuat) {
        this.idUser = idUser;
        this.idTugas = idTugas;
        this.nilaiPerKomponen = nilaiPerKomponen;
        this.isSamaBuat = isSamaBuat;
    }
    
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