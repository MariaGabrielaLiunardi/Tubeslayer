package com.Tubeslayer.dto;

public class PemberianNilaiDTO {
    private Integer idKelompok;
    private String namaKelompok;
    private Integer idTugas;
    private Integer nilai; 
    
    public PemberianNilaiDTO(Integer idKelompok, String namaKelompok, Integer idTugas, Integer nilai) {
        this.idKelompok = idKelompok;
        this.namaKelompok = namaKelompok;
        this.idTugas = idTugas;
        this.nilai = nilai;
    }
    
    public Integer getIdKelompok() {
        return idKelompok;
    }
    
    public void setIdKelompok(Integer idKelompok) {
        this.idKelompok = idKelompok;
    }
    
    public String getNamaKelompok() {
        return namaKelompok;
    }
    
    public void setNamaKelompok(String namaKelompok) {
        this.namaKelompok = namaKelompok;
    }
    
    public Integer getIdTugas() {
        return idTugas;
    }
    
    public void setIdTugas(Integer idTugas) {
        this.idTugas = idTugas;
    }
    
    public Integer getNilai() {
        return nilai;
    }
    
    public void setNilai(Integer nilai) {
        this.nilai = nilai;
    }
}
