package com.Tubeslayer.dto;

/**
 * DTO untuk menggabungkan data Mahasiswa dan Dosen (Koordinator)
 * yang akan ditampilkan di list peserta mata kuliah.
 */
public class PesertaMatkulDTO {
    private int no;
    private String nama;
    private String nomorPokok; // NPM (Mahasiswa) atau NIP (Dosen)
    private String role; // Role: "Koordinator" "Pengampu" atau "Mahasiswa"
    private String kelas; // Hanya untuk Mahasiswa
    
    // Konstruktor untuk Mahasiswa
    public PesertaMatkulDTO(int no, String nama, String nomorPokok, String role, String kelas) {
        this.no = no;
        this.nama = nama;
        this.nomorPokok = nomorPokok;
        this.role = role;
        this.kelas = kelas;
    }
    
    // Konstruktor untuk Dosen/Koordinator 
    public PesertaMatkulDTO(int no, String nama, String nomorPokok, String role) {
        this.no = no;
        this.nama = nama;
        this.nomorPokok = nomorPokok;
        this.role = role;
        this.kelas = null; // Kelas diset null
    }
    
    // Getters and Setters
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

    // toString method
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