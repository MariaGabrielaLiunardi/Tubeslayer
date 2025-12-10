package com.Tubeslayer.dto;

/**
 * DTO untuk response pencarian mahasiswa
 */
public class MahasiswaSearchDTO {
    private String idUser;
    private String nama;
    private String npm; // adalah idUser
    private String kelas;

    // Constructors
    public MahasiswaSearchDTO() {
    }

    public MahasiswaSearchDTO(String idUser, String nama, String kelas) {
        this.idUser = idUser;
        this.nama = nama;
        this.npm = idUser; // NPM = ID User
        this.kelas = kelas;
    }

    // Getters and Setters
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
        this.npm = idUser;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNpm() {
        return npm;
    }

    public void setNpm(String npm) {
        this.npm = npm;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    // toString method untuk debugging
    @Override
    public String toString() {
        return "MahasiswaSearchDTO{" +
                "idUser='" + idUser + '\'' +
                ", nama='" + nama + '\'' +
                ", npm='" + npm + '\'' +
                ", kelas='" + kelas + '\'' +
                '}';
    }
}