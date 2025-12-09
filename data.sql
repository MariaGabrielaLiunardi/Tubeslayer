-- HAPUS database lama kalau ada
DROP DATABASE IF EXISTS tubeslayer;

-- BIKIN database baru
CREATE DATABASE tubeslayer;

-- PAKAI database tersebut
USE tubeslayer;

CREATE TABLE User (
    idUser VARCHAR(30) PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(30) NOT NULL,
    nama VARCHAR(60) NOT NULL,
    role VARCHAR(12) NOT NULL,
    isActive TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE RubrikNilai (
    idRubrik INT AUTO_INCREMENT PRIMARY KEY
) ENGINE=InnoDB;

CREATE TABLE MataKuliah (
    kodeMK VARCHAR(15) PRIMARY KEY,
    nama VARCHAR(50) NOT NULL,
    sks INT NOT NULL,
    isActive TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE TugasBesar (
    idTugas INT AUTO_INCREMENT PRIMARY KEY,
    idUser VARCHAR(30) NOT NULL,
    idRubrik INT NOT NULL,
    kodeMK VARCHAR(15) NOT NULL,
    judulTugas VARCHAR(50) NOT NULL,
    deskripsi VARCHAR(500) NOT NULL,
    deadline DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    modeKel VARCHAR(30) NOT NULL,
    minAnggota INT NOT NULL,
    maxAnggota INT NOT NULL,
    isActive TINYINT(1) NOT NULL DEFAULT 1,
    FOREIGN KEY (idUser) REFERENCES UserTable(idUser),
    FOREIGN KEY (idRubrik) REFERENCES RubrikNilai(idRubrik),
    FOREIGN KEY (kodeMK) REFERENCES MataKuliah(kodeMK)
) ENGINE=InnoDB;

CREATE TABLE Kelompok (
    idKelompok INT AUTO_INCREMENT PRIMARY KEY,
    namaKelompok VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE TugasBesarKelompok (
    idKelompok INT NOT NULL,
    idTugas INT NOT NULL,
    PRIMARY KEY (idKelompok, idTugas),
    FOREIGN KEY (idKelompok) REFERENCES Kelompok(idKelompok),
    FOREIGN KEY (idTugas) REFERENCES TugasBesar(idTugas)
) ENGINE=InnoDB;

CREATE TABLE MataKuliahDosen (
    idUser VARCHAR(30) NOT NULL,
    kodeMK VARCHAR(15) NOT NULL,
    kelas VARCHAR(3) NOT NULL,
    semester INT NOT NULL,
    tahunAkademik VARCHAR(4) NOT NULL,
    isActive TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (idUser, kodeMK),
    FOREIGN KEY (idUser) REFERENCES UserTable(idUser),
    FOREIGN KEY (kodeMK) REFERENCES MataKuliah(kodeMK)
) ENGINE=InnoDB;

CREATE TABLE MataKuliahMahasiswa (
    idUser VARCHAR(30) NOT NULL,
    kodeMK VARCHAR(15) NOT NULL,
    kelas VARCHAR(3) NOT NULL,
    semester INT NOT NULL,
    tahunAkademik VARCHAR(4) NOT NULL,
    isActive TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (idUser, kodeMK),
    FOREIGN KEY (idUser) REFERENCES UserTable(idUser),
    FOREIGN KEY (kodeMK) REFERENCES MataKuliah(kodeMK)
) ENGINE=InnoDB;

CREATE TABLE UserKelompok (
    idUser VARCHAR(30) NOT NULL,
    idKelompok INT NOT NULL,
    role VARCHAR(8) NOT NULL,
    isActive TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (idUser, idKelompok),
    FOREIGN KEY (idUser) REFERENCES UserTable(idUser),
    FOREIGN KEY (idKelompok) REFERENCES Kelompok(idKelompok)
) ENGINE=InnoDB;

CREATE TABLE Nilai (
    idNilai INT AUTO_INCREMENT PRIMARY KEY,
    idUser VARCHAR(30) NOT NULL,
    idTugas INT NOT NULL,
    nilaiPribadi INT NOT NULL,
    nilaiKelompok INT NOT NULL,
    FOREIGN KEY (idUser) REFERENCES UserTable(idUser),
    FOREIGN KEY (idTugas) REFERENCES TugasBesar(idTugas)
) ENGINE=InnoDB;

CREATE TABLE KomponenNilai (
    idKomponen INT AUTO_INCREMENT PRIMARY KEY,
    idRubrik INT NOT NULL,
    namaKomponen VARCHAR(50) NOT NULL,
    bobot INT NOT NULL,
    catatan VARCHAR(300) NOT NULL,
    jam TIME NOT NULL,
    tanggal DATE NOT NULL,
    FOREIGN KEY (idRubrik) REFERENCES RubrikNilai(idRubrik)
) ENGINE=InnoDB;

INSERT INTO rubrik_nilai (id_rubrik) VALUES
(1),(2),(3),(4),(5),(6),(7),(8),(9),(10);

INSERT INTO mata_kuliah (kodemk, nama, sks, isActive) VALUES
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

INSERT INTO tugas_besar (idTugas, idUser, idRubrik, kodeMK, judulTugas, deskripsi, deadline, status, modeKel, minAnggota, maxAnggota, isActive) VALUES
(1, '6182301001', 1, 'IF101', 'Sorting Analyzer', 'Analisis 5 algoritma sorting', '2025-01-20 23:59:00', 'Open', 'Kelompok', 3, 5, 1),
(2, '6182301001', 2, 'IF102', 'Graph Explorer', 'Implementasi BFS dan DFS', '2025-01-25 23:59:00', 'Open', 'Kelompok', 2, 4, 1),
(3, '6182301005', 3, 'IF201', 'DB Design', 'Membuat ERD dan relasi database', '2025-01-22 23:59:00', 'Open', 'Individu', 1, 1, 1),
(4, '6182301005', 4, 'IF202', 'Packet Sniffer', 'Analisis traffic jaringan', '2025-02-01 23:59:00', 'Open', 'Kelompok', 3, 6, 1),
(5, '6182301001', 5, 'IF203', 'Web Portfolio', 'Membangun website portofolio', '2025-02-03 23:59:00', 'Open', 'Individu', 1, 1, 1),
(6, '6182301005', 6, 'IF204', 'Mobile Finance App', 'Aplikasi catatan keuangan', '2025-01-28 23:59:00', 'Open', 'Kelompok', 2, 5, 1),
(7, '6182301001', 7, 'IF205', 'CPU Scheduler', 'Simulasi algoritma penjadwalan CPU', '2025-01-30 23:59:00', 'Open', 'Individu', 1, 1, 1),
(8, '6182301005', 8, 'IF301', 'ML Regression', 'Implementasi linear regression', '2025-02-10 23:59:00', 'Open', 'Kelompok', 2, 4, 1),
(9, '6182301001', 9, 'IF302', 'Expert System', 'Sistem pakar diagnosa tanaman', '2025-02-15 23:59:00', 'Open', 'Kelompok', 3, 6, 1),
(10,'6182301005',10, 'IF303', 'SRS Document', 'Membuat SRS untuk aplikasi tertentu', '2025-02-12 23:59:00', 'Open', 'Kelompok', 3, 5, 1);

INSERT INTO kelompok (idKelompok, namaKelompok) VALUES
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
('6182301001', 'IF101', 'A', 1, '2025', 1),
('6182301001', 'IF102', 'A', 1, '2025', 1),
('6182301001', 'IF203', 'B', 2, '2025', 1),
('6182301001', 'IF205', 'A', 3, '2025', 1),
('6182301001', 'IF302', 'C', 3, '2025', 1),
('6182301005', 'IF201', 'A', 1, '2025', 1),
('6182301005', 'IF202', 'B', 2, '2025', 1),
('6182301005', 'IF204', 'A', 2, '2025', 1),
('6182301005', 'IF301', 'C', 3, '2025', 1),
('6182301005', 'IF303', 'B', 3, '2025', 1);

INSERT INTO mata_kuliah_mahasiswa VALUES
('6182301002','IF101','A',1,'2025',1),
('6182301003','IF101','A',1,'2025',1),
('6182301004','IF101','A',1,'2025',1),
('6182301006','IF102','A',1,'2025',1),
('6182301007','IF102','A',1,'2025',1),
('6182301008','IF203','B',2,'2025',1),
('6182301009','IF203','B',2,'2025',1),
('6182301010','IF205','A',3,'2025',1),
('6182301006','IF301','C',3,'2025',1),
('6182301007','IF303','B',3,'2025',1);

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

INSERT INTO nilai (idNilai, idUser, idTugas, nilaiPribadi, nilaiKelompok) VALUES
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


