-- HAPUS database lama kalau ada
DROP DATABASE IF EXISTS tubeslayer;

-- BUAT database baru
CREATE DATABASE tubeslayer;

-- GUNAKAN database tubeslayer
USE tubeslayer;

CREATE TABLE user_table (
    id_user VARCHAR(30) PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nama VARCHAR(60) NOT NULL,
    role VARCHAR(12) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE rubrik_nilai (
    id_rubrik INT AUTO_INCREMENT PRIMARY KEY
);

CREATE TABLE mata_kuliah (
    kode_mk VARCHAR(15) PRIMARY KEY,
    nama VARCHAR(50) NOT NULL,
    sks INT NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE tugas_besar (
    id_tugas INT AUTO_INCREMENT PRIMARY KEY,
    id_user VARCHAR(30) NOT NULL,
    id_rubrik INT NOT NULL,
    kode_mk VARCHAR(15) NOT NULL,
    judul_tugas VARCHAR(50) NOT NULL,
    deskripsi VARCHAR(500) NOT NULL,
    deadline DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    mode_kel VARCHAR(30) NOT NULL,
    min_anggota INT NOT NULL,
    max_anggota INT NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    FOREIGN KEY (id_user) REFERENCES user_table(id_user),
    FOREIGN KEY (id_rubrik) REFERENCES rubrik_nilai(id_rubrik),
    FOREIGN KEY (kode_mk) REFERENCES mata_kuliah(kode_mk)
);

CREATE TABLE kelompok (
    id_kelompok INT AUTO_INCREMENT PRIMARY KEY,
    nama_kelompok VARCHAR(50) NOT NULL
);
CREATE TABLE tugas_besar_kelompok (
    id_kelompok INT NOT NULL,
    id_tugas INT NOT NULL,
    PRIMARY KEY (id_kelompok, id_tugas),
    FOREIGN KEY (id_kelompok) REFERENCES kelompok(id_kelompok),
    FOREIGN KEY (id_tugas) REFERENCES tugas_besar(id_tugas)
);

CREATE TABLE mata_kuliah_dosen (
    id_user VARCHAR(30) NOT NULL,
    kode_mk VARCHAR(15) NOT NULL,
    kelas VARCHAR(3) NOT NULL,
    semester INT NOT NULL,
    tahun_akademik VARCHAR(10) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_user, kode_mk),
    FOREIGN KEY (id_user) REFERENCES user_table(id_user),
    FOREIGN KEY (kode_mk) REFERENCES mata_kuliah(kode_mk)
);

CREATE TABLE mata_kuliah_mahasiswa (
    id_user VARCHAR(30) NOT NULL,
    kode_mk VARCHAR(15) NOT NULL,
    kelas VARCHAR(3) NOT NULL,
    semester INT NOT NULL,
    tahun_akademik VARCHAR(10) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_user, kode_mk),
    FOREIGN KEY (id_user) REFERENCES user_table(id_user),
    FOREIGN KEY (kode_mk) REFERENCES mata_kuliah(kode_mk)
);

CREATE TABLE user_kelompok (
    id_user VARCHAR(30) NOT NULL,
    id_kelompok INT NOT NULL,
    role VARCHAR(8) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_user, id_kelompok),
    FOREIGN KEY (id_user) REFERENCES user_table(id_user),
    FOREIGN KEY (id_kelompok) REFERENCES kelompok(id_kelompok)
);

CREATE TABLE nilai (
    id_nilai INT AUTO_INCREMENT PRIMARY KEY,
    id_user VARCHAR(30) NOT NULL,
    id_tugas INT NOT NULL,
    nilai_pribadi INT NOT NULL,
    nilai_kelompok INT NOT NULL,
    FOREIGN KEY (id_user) REFERENCES user_table(id_user),
    FOREIGN KEY (id_tugas) REFERENCES tugas_besar(id_tugas)
);

CREATE TABLE nilai_komponen (
    id_nilai_komponen INT AUTO_INCREMENT PRIMARY KEY,
    id_nilai INT NOT NULL,
    id_komponen INT NOT NULL,
    nilai_komponen INT NOT NULL,
    UNIQUE KEY uk_nilai_komponen (id_nilai, id_komponen),
    FOREIGN KEY (id_nilai) REFERENCES nilai(id_nilai),
    FOREIGN KEY (id_komponen) REFERENCES komponen_nilai(id_komponen)
);

CREATE TABLE komponen_nilai (
    id_komponen INT AUTO_INCREMENT PRIMARY KEY,
    id_rubrik INT NOT NULL,
    nama_komponen VARCHAR(50) NOT NULL,
    bobot INT NOT NULL,
    catatan VARCHAR(300) NULL,
    FOREIGN KEY (id_rubrik) REFERENCES rubrik_nilai(id_rubrik)
);

-- PASTIKAN DATABASE tubeslayer SUDAH ADA & USE tubeslayer;
USE tubeslayer;

-- Jika tabel jadwal_penilaian belum ada, buat:
CREATE TABLE IF NOT EXISTS jadwal_penilaian (
    id_jadwal INT AUTO_INCREMENT PRIMARY KEY,
    id_rubrik INT NOT NULL,
    tanggal DATE NOT NULL,
    jam TIME NOT NULL,
    ruangan VARCHAR(50),
    FOREIGN KEY (id_rubrik) REFERENCES rubrik_nilai(id_rubrik)
);

ALTER TABLE mata_kuliah_dosen
DROP PRIMARY KEY,
ADD PRIMARY KEY (id_user, kode_mk, kelas, semester, tahun_akademik);

ALTER TABLE mata_kuliah_mahasiswa
DROP PRIMARY KEY,
ADD PRIMARY KEY (id_user, kode_mk, kelas, semester, tahun_akademik);