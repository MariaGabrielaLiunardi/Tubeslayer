package com.Tubeslayer.dto;

public class PesertaMatkulDTO {
    private int no;
    private String nama;
    private String nomorPokok;
    private String role;
    private String kelas;
    
    public PesertaMatkulDTO(int no, String nama, String nomorPokok, String role, String kelas) {
        this.no = no;
        this.nama = nama;
        this.nomorPokok = nomorPokok;
        this.role = role;
        this.kelas = kelas;
    }
    
    public PesertaMatkulDTO(int no, String nama, String nomorPokok, String role) {
        this.no = no;
        this.nama = nama;
        this.nomorPokok = nomorPokok;
        this.role = role;
        this.kelas = null;
    }
    
    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNomorPokok() {
        return nomorPokok;
    }

    public void setNomorPokok(String nomorPokok) {
        this.nomorPokok = nomorPokok;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    @Override
    public String toString() {
        return "PesertaMatkulDTO{" +
                "no=" + no +
                ", nama='" + nama + '\'' +
                ", nomorPokok='" + nomorPokok + '\'' +
                ", role='" + role + '\'' +
                ", kelas='" + kelas + '\'' +
                '}';
    }
}