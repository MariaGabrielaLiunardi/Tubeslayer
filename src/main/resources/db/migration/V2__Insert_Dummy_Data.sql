-- V2__Insert_Dummy_Data.sql
-- Insert all dummy data

-- === USERS (admin, dosen, mahasiswa) ===
INSERT IGNORE INTO user_table (id_user, email, password, nama, role, is_active) VALUES
('20250199','admin@unpar.ac.id','$2a$12$JVCmMGpDmD1yd7d7eOmDAe1oSRUD13rNu4L7VNBUW2IDX3GDGZ1Bi','Admin Sistem','Admin',1),
('20250101','agus@unpar.ac.id','$2a$12$jsq4xkNTSQQKF6O5f3ctsuxSuxXSEgu1ULt5ugH.xKBHTmDxllum2','Agus Santoso','Dosen',1),
('20250102','maria@unpar.ac.id','$2a$12$jsq4xkNTSQQKF6O5f3ctsuxSuxXSEgu1ULt5ugH.xKBHTmDxllum2','Maria Lestari','Dosen',1),
('20250103','budi@unpar.ac.id','$2a$12$jsq4xkNTSQQKF6O5f3ctsuxSuxXSEgu1ULt5ugH.xKBHTmDxllum2','Budi Pranoto','Dosen',1),
('6182301001','adi@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Aditya Putra','Mahasiswa',1),
('6182301002','bella@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Bella Anggraini','Mahasiswa',1),
('6182301003','cahya@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Cahya Ramadhan','Mahasiswa',1),
('6182301004','dina@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Dina Kusuma','Mahasiswa',1),
('6182301005','eko@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Eko Wibowo','Mahasiswa',1),
('6182301006','fina@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Fina Marlina','Mahasiswa',1),
('6182301007','gilang@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Gilang Pratama','Mahasiswa',1),
('6182301008','hesti@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Hesti Nur','Mahasiswa',1),
('6182301009','iqbal@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Iqbal Ramadhan','Mahasiswa',1),
('6182301010','joni@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Joni Saputra','Mahasiswa',1),
('6182301011','kiki@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Kiki Amalia','Mahasiswa',1),
('6182301012','lina@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Lina Sari','Mahasiswa',1),
('6182301013','miko@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Miko Santoso','Mahasiswa',1),
('6182301014','nina@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Nina Dewi','Mahasiswa',1),
('6182301015','omar@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Omar Hakim','Mahasiswa',1),
('6182301016','putri@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Putri Cahaya','Mahasiswa',1),
('6182301017','qori@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Qori Hidayat','Mahasiswa',1),
('6182301018','rani@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Rani Maulani','Mahasiswa',1),
('6182301019','sandi@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Sandi Permana','Mahasiswa',1),
('6182301020','tika@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Tika Lestari','Mahasiswa',1),
('6182301021','udin@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Udin Santoso','Mahasiswa',1),
('6182301022','vika@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Vika Amelia','Mahasiswa',0),
('6182301023','wawan@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Wawan Kurnia','Mahasiswa',1),
('6182301024','xenia@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Xenia Putri','Mahasiswa',1),
('6182301025','yoga@student.unpar.ac.id','$2a$12$77JXn7V/zlNdSPHj1Nx6huH6OsDvlyDFzkIPebs7KdrTUdCmXvNf6','Yoga Pratama','Mahasiswa',1);

-- === Rubrik ===
INSERT IGNORE INTO rubrik_nilai (id_rubrik) VALUES (1),(2),(3),(4),(5),(6),(7),(8),(9),(10),(11),(12),(13),(14),(15),(16),(17),(18),(19),(20);

-- === Mata Kuliah ===
INSERT IGNORE INTO mata_kuliah (kode_mk, nama, sks, is_active) VALUES
('AIF23001','Algoritma dan Pemrograman',3,1),
('AIF23002','Struktur Data',3,1),
('AIF23003','Basis Data',3,1),
('AIF23004','Pemrograman Web',3,1),
('AIF23005','Pemrograman Mobile',3,1),
('AIF23921','Matematika Diskrit',3,0),
('AIF23922','Kalkulus',3,0),
('AIF23923','Etika Profesi',2,0),
('AIF23924','Sejarah Teknologi',2,0),
('AIF23925','Bahasa Indonesia Teknis',2,0);

-- === Mata Kuliah Dosen ===
INSERT IGNORE INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES
('20250101','AIF23001','A',1,'2025/2026',1),
('20250101','AIF23002','A',1,'2025/2026',1),
('20250102','AIF23003','A',1,'2025/2026',1),
('20250102','AIF23004','B',1,'2025/2026',1),
('20250103','AIF23005','A',1,'2025/2026',1);

-- === Mata Kuliah Mahasiswa ===
INSERT IGNORE INTO mata_kuliah_mahasiswa (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES
('6182301001','AIF23001','A',1,'2025/2026',1),
('6182301002','AIF23001','A',1,'2025/2026',1),
('6182301003','AIF23001','A',1,'2025/2026',1),
('6182301004','AIF23001','A',1,'2025/2026',1),
('6182301005','AIF23001','A',1,'2025/2026',1),
('6182301006','AIF23001','A',1,'2025/2026',1),
('6182301007','AIF23001','A',1,'2025/2026',1),
('6182301008','AIF23001','A',1,'2025/2026',1),
('6182301009','AIF23001','A',1,'2025/2026',1),
('6182301010','AIF23001','A',1,'2025/2026',1),
('6182301011','AIF23001','A',1,'2025/2026',1),
('6182301012','AIF23001','A',1,'2025/2026',1),
('6182301003','AIF23002','A',1,'2025/2026',1),
('6182301004','AIF23002','A',1,'2025/2026',1),
('6182301005','AIF23002','A',1,'2025/2026',1),
('6182301006','AIF23002','A',1,'2025/2026',1),
('6182301007','AIF23002','A',1,'2025/2026',1),
('6182301008','AIF23002','A',1,'2025/2026',1),
('6182301009','AIF23002','A',1,'2025/2026',1),
('6182301010','AIF23002','A',1,'2025/2026',1),
('6182301011','AIF23002','A',1,'2025/2026',1),
('6182301012','AIF23002','A',1,'2025/2026',1),
('6182301013','AIF23002','A',1,'2025/2026',1),
('6182301014','AIF23002','A',1,'2025/2026',1),
('6182301005','AIF23003','B',1,'2025/2026',1),
('6182301006','AIF23003','B',1,'2025/2026',1),
('6182301007','AIF23003','B',1,'2025/2026',1),
('6182301008','AIF23003','B',1,'2025/2026',1),
('6182301009','AIF23003','B',1,'2025/2026',1),
('6182301010','AIF23003','B',1,'2025/2026',1),
('6182301011','AIF23003','B',1,'2025/2026',1),
('6182301012','AIF23003','B',1,'2025/2026',1),
('6182301013','AIF23003','B',1,'2025/2026',1),
('6182301014','AIF23003','B',1,'2025/2026',1),
('6182301015','AIF23003','B',1,'2025/2026',1),
('6182301016','AIF23003','B',1,'2025/2026',1),
('6182301007','AIF23004','B',2,'2025/2026',1),
('6182301008','AIF23004','B',2,'2025/2026',1),
('6182301009','AIF23004','B',2,'2025/2026',1),
('6182301010','AIF23004','B',2,'2025/2026',1),
('6182301011','AIF23004','B',2,'2025/2026',1),
('6182301012','AIF23004','B',2,'2025/2026',1),
('6182301013','AIF23004','B',2,'2025/2026',1),
('6182301014','AIF23004','B',2,'2025/2026',1),
('6182301015','AIF23004','B',2,'2025/2026',1),
('6182301016','AIF23004','B',2,'2025/2026',1),
('6182301017','AIF23004','B',2,'2025/2026',1),
('6182301018','AIF23004','B',2,'2025/2026',1),
('6182301009','AIF23005','A',2,'2025/2026',1),
('6182301010','AIF23005','A',2,'2025/2026',1),
('6182301011','AIF23005','A',2,'2025/2026',1),
('6182301012','AIF23005','A',2,'2025/2026',1),
('6182301013','AIF23005','A',2,'2025/2026',1),
('6182301014','AIF23005','A',2,'2025/2026',1),
('6182301015','AIF23005','A',2,'2025/2026',1),
('6182301016','AIF23005','A',2,'2025/2026',1),
('6182301017','AIF23005','A',2,'2025/2026',1),
('6182301018','AIF23005','A',2,'2025/2026',1),
('6182301019','AIF23005','A',2,'2025/2026',1),
('6182301020','AIF23005','A',2,'2025/2026',1),
('6182301021','AIF23005','A',2,'2025/2026',1),
('6182301023','AIF23005','A',2,'2025/2026',1),
('6182301024','AIF23005','A',2,'2025/2026',1),
('6182301025','AIF23005','A',2,'2025/2026',1);

-- === Kelompok ===
INSERT IGNORE INTO kelompok (nama_kelompok) VALUES
('Kelompok A1'),('Kelompok A2'),('Kelompok A3'),
('Kelompok B1'),('Kelompok B2'),('Kelompok B3'),
('Kelompok C1'),('Kelompok C2'),('Kelompok C3'),
('Kelompok D1'),('Kelompok D2'),('Kelompok D3'),
('Kelompok E1'),('Kelompok E2'),('Kelompok E3'),
('Kelompok F1'),('Kelompok F2'),('Kelompok F3'),
('Kelompok G1'),('Kelompok G2'),('Kelompok G3'),
('Kelompok H1'),('Kelompok H2'),('Kelompok H3'),
('Kelompok I1'),('Kelompok I2'),('Kelompok I3'),
('Kelompok J1'),('Kelompok J2'),('Kelompok J3');

-- === Tugas Besar ===
INSERT IGNORE INTO tugas_besar (id_user, id_rubrik, kode_mk, judul_tugas, deskripsi, deadline, status, mode_kel, min_anggota, max_anggota, is_active) VALUES
('20250101',1,'AIF23001','TB Sorting','Analisis dan perbandingan algoritma sorting','2025-01-20 23:59:00','Open','Dosen',3,5,1),
('20250101',2,'AIF23001','TB Searching','Analisis linear vs binary search','2025-02-01 23:59:00','Open','Mahasiswa',2,4,1),
('20250101',3,'AIF23002','TB Graph','Implementasi BFS & DFS','2025-01-22 23:59:00','Open','Mahasiswa',2,4,1),
('20250101',4,'AIF23002','TB Tree','Analisis pohon AVL & red-black','2025-02-05 23:59:00','Open','Dosen',3,5,1),
('20250102',5,'AIF23003','TB ERD','Desain ERD & normalisasi','2025-01-25 23:59:00','Open','Dosen',1,2,1),
('20250102',6,'AIF23003','TB Normalisasi','Kasus normalisasi sampai 3NF','2025-02-10 23:59:00','Open','Mahasiswa',1,3,1),
('20250102',7,'AIF23004','TB Web','Pembuatan website portofolio','2025-02-01 23:59:00','Open','Dosen',2,4,1),
('20250102',8,'AIF23004','TB API','Desain & dokumentasi REST API','2025-02-20 23:59:00','Open','Mahasiswa',2,4,1),
('20250103',9,'AIF23005','TB Mobile','Aplikasi mobile catatan keuangan','2025-01-28 23:59:00','Open','Dosen',2,5,1),
('20250103',10,'AIF23005','TB UX Mobile','Studi UX & prototipe mobile','2025-02-15 23:59:00','Open','Mahasiswa',1,3,1);

-- === Tugas Besar Kelompok ===
INSERT IGNORE INTO tugas_besar_kelompok (id_kelompok, id_tugas) VALUES
(1,1),(2,1),(3,1),
(4,2),(5,2),(6,2),
(7,3),(8,3),(9,3),
(10,4),(11,4),(12,4),
(13,5),(14,5),(15,5),
(16,6),(17,6),(18,6),
(19,7),(20,7),(21,7),
(22,8),(23,8),(24,8),
(25,9),(26,9),(27,9),
(28,10),(29,10),(30,10);

-- === User Kelompok ===
INSERT IGNORE INTO user_kelompok (id_user, id_kelompok, role, is_active) VALUES
('6182301001',1,'leader',1),('6182301002',1,'member',1),('6182301003',1,'member',1),
('6182301004',2,'leader',1),('6182301005',2,'member',1),('6182301006',2,'member',1),('6182301007',2,'member',1),
('6182301008',3,'leader',1),('6182301009',3,'member',1),('6182301010',3,'member',1),
('6182301011',4,'leader',1),('6182301012',4,'member',1),
('6182301013',5,'leader',1),('6182301014',5,'member',1),('6182301015',5,'member',1),
('6182301016',6,'leader',1),('6182301017',6,'member',1),('6182301018',6,'member',1),
('6182301003',7,'leader',1),('6182301004',7,'member',1),
('6182301005',8,'leader',1),('6182301006',8,'member',1),('6182301007',8,'member',1),
('6182301008',9,'leader',1),('6182301009',9,'member',1),('6182301010',9,'member',1),
('6182301011',10,'leader',1),('6182301012',10,'member',1),('6182301013',10,'member',1),
('6182301014',11,'leader',1),('6182301015',11,'member',1),('6182301016',11,'member',1),('6182301017',11,'member',1),
('6182301018',12,'leader',1),('6182301019',12,'member',1),('6182301020',12,'member',1),
('6182301005',13,'leader',1),
('6182301006',14,'leader',1),('6182301007',14,'member',1),
('6182301008',15,'leader',1),('6182301009',15,'member',1),
('6182301010',16,'leader',1),('6182301011',16,'member',1),
('6182301012',17,'leader',1),('6182301013',17,'member',1),('6182301014',17,'member',1),
('6182301015',18,'leader',1),
('6182301007',19,'leader',1),('6182301008',19,'member',1),('6182301009',19,'member',1),
('6182301010',20,'leader',1),('6182301011',20,'member',1),
('6182301012',21,'leader',1),('6182301013',21,'member',1),('6182301014',21,'member',1),
('6182301015',22,'leader',1),('6182301016',22,'member',1),
('6182301017',23,'leader',1),('6182301018',23,'member',1),('6182301019',23,'member',1),
('6182301020',24,'leader',1),('6182301021',24,'member',1),
('6182301009',25,'leader',1),('6182301010',25,'member',1),('6182301011',25,'member',1),('6182301012',25,'member',1),
('6182301013',26,'leader',1),('6182301014',26,'member',1),('6182301015',26,'member',1),
('6182301016',27,'leader',1),('6182301017',27,'member',1),('6182301018',27,'member',1),
('6182301019',28,'leader',1),('6182301020',28,'member',1),
('6182301021',29,'leader',1),('6182301023',29,'member',1),
('6182301024',30,'leader',1),('6182301025',30,'member',1);

-- === Nilai ===
INSERT IGNORE INTO nilai (id_user, id_tugas, nilai_pribadi, nilai_kelompok) VALUES
('6182301001',1,88,90),
('6182301002',1,85,90),
('6182301003',1,80,90),
('6182301004',2,92,88),
('6182301005',2,87,88),
('6182301006',3,90,0),
('6182301007',4,95,0),
('6182301008',5,78,85),
('6182301009',6,82,80),
('6182301010',7,88,92),
('6182301011',8,75,80),
('6182301012',9,70,78),
('6182301013',10,93,95);

-- === Komponen Nilai ===
INSERT IGNORE INTO komponen_nilai (id_rubrik, nama_komponen, bobot, catatan, jam, tanggal) VALUES
(1,'Laporan',40,'Laporan lengkap','10:00:00','2025-01-15'),
(1,'Presentasi',30,'Presentasi 10 menit','13:00:00','2025-01-15'),
(2,'Koding',50,'Implementasi sesuai standar','09:00:00','2025-01-18');

-- === Jadwal Penilaian ===
INSERT IGNORE INTO jadwal_penilaian (id_rubrik, tanggal, jam, ruangan) VALUES
(1,'2025-01-15','10:00:00','R101'),
(2,'2025-01-18','09:00:00','Lab 1');
