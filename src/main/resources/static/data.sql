-- HAPUS database lama kalau ada
DROP DATABASE IF EXISTS tubeslayer;

-- BIKIN database baru
CREATE DATABASE tubeslayer;

-- PAKAI database tersebut
USE tubeslayer;

SHOW Tables; 

select * 
from mata_kuliah_mahasiswa; 

select * 
from user_table;  

select * 
from mata_kuliah; 

select * from user_table; 

show create table user_kelompok;

select * from user_table; 

ALTER TABLE tugas_besar_kelompok
DROP FOREIGN KEY FK8ol2m7n810n562aqcdfbu1b3f; 
ALTER TABLE tugas_besar_kelompok
DROP FOREIGN KEY tugas_besar_kelompok_ibfk_1; 
ALTER TABLE tugas_besar_kelompok
DROP FOREIGN KEY tugas_besar_kelompok_ibfk_2; 


SHOW TABLES;

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

CREATE TABLE komponen_nilai (
    id_komponen INT AUTO_INCREMENT PRIMARY KEY,
    id_rubrik INT NOT NULL,
    nama_komponen VARCHAR(50) NOT NULL,
    bobot INT NOT NULL,
    catatan VARCHAR(300) NOT NULL,
    jam TIME NOT NULL,
    tanggal DATE NOT NULL,
    FOREIGN KEY (id_rubrik) REFERENCES rubrik_nilai(id_rubrik)
);

INSERT INTO user_table (id_user, email, password, nama, role, is_active) VALUES
('6182301001', 'andi@unpar.ac.id', '$2a$10$lpXunJk2Te8/hHcfFFmpduViPATPUYuau.rAK1ckJbpDh5m8MSXV2', 'Andi Pratama', 'Dosen', 1),
('6182301002', 'budi@student.unpar.ac.id', '$2a$10$8fAQ94qX0o1GiPJbKVuXBOcStH4rvC/N0ZnDlp.H3aMoBU/2toeMC', 'Budi Santoso', 'Mahasiswa', 1),
('6182301003', 'citra@estudent.unpar.ac.id', '$2a$10$8fAQ94qX0o1GiPJbKVuXBOcStH4rvC/N0ZnDlp.H3aMoBU/2toeMC', 'Citra Lestari', 'Mahasiswa', 1),
('6182301004', 'dewistudent.unpar.ac.id', '$2a$10$8fAQ94qX0o1GiPJbKVuXBOcStH4rvC/N0ZnDlp.H3aMoBU/2toeMC', 'Dewi Anggraini', 'Mahasiswa', 1),
('6182301005', 'eko@unpar.ac.id', '$2a$10$lpXunJk2Te8/hHcfFFmpduViPATPUYuau.rAK1ckJbpDh5m8MSXV2', 'Eko Wijaya', 'Dosen', 1),
('6182301006', 'fina@student.unpar.ac.id', '$2a$10$8fAQ94qX0o1GiPJbKVuXBOcStH4rvC/N0ZnDlp.H3aMoBU/2toeMC', 'Fina Kusuma', 'Mahasiswa', 1),
('6182301007', 'galih@student.unpar.ac.id', '$2a$10$8fAQ94qX0o1GiPJbKVuXBOcStH4rvC/N0ZnDlp.H3aMoBU/2toeMC', 'Galih Putra', 'Mahasiswa', 1),
('6182301008', 'hana@student.unpar.ac.id', '$2a$10$8fAQ94qX0o1GiPJbKVuXBOcStH4rvC/N0ZnDlp.H3aMoBU/2toeMC', 'Hana Aprilia', 'Mahasiswa', 1),
('6182301009', 'indra@student.unpar.ac.id', '$2a$10$8fAQ94qX0o1GiPJbKVuXBOcStH4rvC/N0ZnDlp.H3aMoBU/2toeMC', 'Indra Maulana', 'Mahasiswa', 1),
('6182301010', 'joni@student.unpar.ac.id', '$2a$10$8fAQ94qX0o1GiPJbKVuXBOcStH4rvC/N0ZnDlp.H3aMoBU/2toeMC', 'Joni Saputra', 'Mahasiswa', 1);

INSERT INTO rubrik_nilai (id_rubrik) VALUES
(1),(2),(3),(4),(5),(6),(7),(8),(9),(10);

INSERT INTO mata_kuliah (kode_mk, nama, sks, is_active) VALUES
('IF101', 'Algoritma', 3, 1),
('IF102', 'Struktur Data', 3, 1),
('IF201', 'Basis Data', 3, 1),
('IF202', 'Jaringan Komputer', 3, 1),
('IF203', 'Pemrograman Web', 3, 1),
('IF204', 'Pemrograman Mobile', 3, 1),
('IF205', 'Sistem Operasi', 3, 1),
('IF301', 'Machine Learning', 3, 1),
('IF302', 'Kecerdasan Buatan', 3, 1),
('IF303', 'Rekayasa Perangkat Lunak', 3, 1); 

INSERT INTO mata_kuliah (kode_mk, nama, sks, is_active) VALUES
('IF707', 'Sistem Big Data untuk Organisasi', 3, 0),
('IF701', 'Rekayasa Perangkat Lunak', 4, 0), 
('IF702', 'Statistika dengan R', 3, 0), 
('IF703', 'Machine Learning', 3, 0); 

SELECT *
FROM mata_kuliah mk inner join mata_kuliah_dosen md on
	mk.kode_mk = md.kode_mk
WHERE 
	mk.is_active = false;

DESCRIBE tugas_besar;

INSERT INTO tugas_besar (id_tugas, id_user, id_rubrik, kode_mk, judul_tugas, deskripsi, deadline, status, mode_kel, min_anggota, max_anggota, is_active) VALUES
(1, '6182301002', 1, 'IF101', 'Sorting Analyzer', 'Analisis 5 algoritma sorting', '2025-01-20 23:59:00', 'Open', 'Dosen', 3, 5, 1),
(2, '6182301003', 2, 'IF102', 'Graph Explorer', 'Implementasi BFS dan DFS', '2025-01-25 23:59:00', 'Open', 'Mahasiswa', 2, 4, 1),
(3, '6182301004', 3, 'IF201', 'DB Design', 'Membuat ERD dan relasi database', '2025-01-22 23:59:00', 'Open', 'Mahasiswa', 1, 1, 1),
(4, '6182301006', 4, 'IF202', 'Packet Sniffer', 'Analisis traffic jaringan', '2025-02-01 23:59:00', 'Open', 'Dosen', 3, 6, 1),
(5, '6182301007', 5, 'IF203', 'Web Portfolio', 'Membangun website portofolio', '2025-02-03 23:59:00', 'Open', 'Dosen', 1, 1, 1),
(6, '6182301008', 6, 'IF204', 'Mobile Finance App', 'Aplikasi catatan keuangan', '2025-01-28 23:59:00', 'Open', 'Dosen', 2, 5, 1),
(7, '6182301009', 7, 'IF205', 'CPU Scheduler', 'Simulasi algoritma penjadwalan CPU', '2025-01-30 23:59:00', 'Open', 'Mahasiswa', 1, 1, 1),
(8, '6182301010', 8, 'IF301', 'ML Regression', 'Implementasi linear regression', '2025-02-10 23:59:00', 'Open', 'Dosen', 2, 4, 1),
(9, '6182301002', 9, 'IF302', 'Expert System', 'Sistem pakar diagnosa tanaman', '2025-02-15 23:59:00', 'Open', 'Mahasiswa', 3, 6, 1),
(10,'6182301003',10, 'IF303', 'SRS Document', 'Membuat SRS untuk aplikasi tertentu', '2025-02-12 23:59:00', 'Open', 'Dosen', 3, 5, 1);

INSERT INTO kelompok (id_kelompok, nama_kelompok) VALUES
(1, 'Kelompok Alpha'),
(2, 'Kelompok Beta'),
(3, 'Kelompok Gamma'),
(4, 'Kelompok Delta'),
(5, 'Kelompok Sigma'),
(6, 'Kelompok Omega'),
(7, 'Kelompok Orion'),
(8, 'Kelompok Titan'),
(9, 'Kelompok Nova'),
(10, 'Kelompok Nebula');

INSERT INTO tugas_besar_kelompok VALUES
(1,1),(2,2),(3,4),(4,6),(5,8),
(6,9),(7,10),(8,3),(9,5),(10,7);

INSERT INTO mata_kuliah_dosen VALUES
('6182301001', 'IF101', 'A', 1, '2025/2026', 1),
('6182301001', 'IF102', 'A', 1, '2025/2026', 1),
('6182301001', 'IF203', 'B', 2, '2025/2026', 1),
('6182301001', 'IF205', 'A', 3, '2025/2026', 1),
('6182301001', 'IF302', 'C', 3, '2025/2026', 1),
('6182301005', 'IF201', 'A', 1, '2025/2026', 1),
('6182301005', 'IF202', 'B', 2, '2025/2026', 1),
('6182301005', 'IF204', 'A', 2, '2025/2026', 1),
('6182301005', 'IF301', 'C', 3, '2025/2026', 1),
('6182301005', 'IF303', 'B', 3, '2025/2026', 1);

INSERT INTO mata_kuliah_dosen VALUES
('6182301001', 'IF707', 'A', 1, '2023/2024', 1); 

INSERT INTO mata_kuliah_mahasiswa VALUES
('6182301002','IF101','A',1,'2025/2026',1),
('6182301003','IF101','A',1,'2025/2026',1),
('6182301004','IF101','A',1,'2025/2026',1),
('6182301006','IF102','A',1,'2025/2026',1),
('6182301007','IF102','A',1,'2025/2026',1),
('6182301008','IF203','B',2,'2025/2026',1),
('6182301009','IF203','B',2,'2025/2026',1),
('6182301010','IF205','A',3,'2025/2026',1),
('6182301006','IF301','C',3,'2025/2026',1),
('6182301007','IF303','B',3,'2025/2026',1);

INSERT INTO user_kelompok VALUES
('6182301002',1,'member',1),
('6182301003',1,'leader',1),
('6182301004',2,'member',1),
('6182301006',2,'leader',1),
('6182301007',3,'member',1),
('6182301008',4,'leader',1),
('6182301009',5,'member',1),
('6182301010',6,'leader',1),
('6182301006',7,'member',1),
('6182301007',8,'leader',1);

INSERT INTO Nilai (id_nilai, id_user, id_tugas, nilai_pribadi, nilai_kelompok) VALUES
(1,'6182301002',1,85,90),
(2,'6182301003',1,88,90),
(3,'6182301004',2,80,85),
(4,'6182301006',2,75,85),
(5,'6182301007',3,90,0),
(6,'6182301008',4,70,80),
(7,'6182301009',6,65,78),
(8,'6182301010',7,95,0),
(9,'6182301006',8,87,92),
(10,'6182301007',9,92,94);

INSERT INTO komponen_nilai VALUES
(1,1,'Laporan',40,'Laporan lengkap','10:00:00','2025-01-15'),
(2,1,'Presentasi',30,'Presentasi 10 menit','10:30:00','2025-01-15'),
(3,2,'Koding',50,'Implementasi sesuai standar','09:00:00','2025-01-18'),
(4,3,'ERD',40,'ERD harus lengkap','11:00:00','2025-01-20'),
(5,4,'Analisis',60,'Analisis network capture','13:00:00','2025-01-25'),
(6,5,'Website',70,'Frontend dan backend','14:00:00','2025-01-28'),
(7,6,'Prototype',50,'UI sederhana','16:00:00','2025-01-30'),
(8,7,'Simulasi',60,'Simulasi 3 algoritma','08:00:00','2025-02-01'),
(9,8,'Training',50,'Hasil regresi','09:30:00','2025-02-03'),
(10,9,'Rule Base',40,'Rule lengkap','10:30:00','2025-02-05');

