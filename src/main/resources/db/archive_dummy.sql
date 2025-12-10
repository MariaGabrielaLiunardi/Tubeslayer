-- ========================================
-- DATA DUMMY UNTUK MATA KULIAH ARCHIVE
-- Gunakan ID yang lebih tinggi untuk menghindari konflik
-- ========================================

USE tubeslayer;

-- === TAMBAHAN RUBRIK UNTUK TUGAS ARCHIVE (ID 101-120) ===
-- Gunakan ID mulai dari 101 agar tidak bentrok dengan data existing (1-20)
INSERT INTO rubrik_nilai (id_rubrik) VALUES 
(101),(102),(103),(104),(105),(106),(107),(108),(109),(110),
(111),(112),(113),(114),(115),(116),(117),(118),(119),(120);

-- === RELASI DOSEN KE MATA KULIAH ARCHIVE ===
INSERT INTO mata_kuliah_dosen (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES
('20250101','AIF23921','A',1,'2023/2024',0),
('20250101','AIF23921','B',1,'2023/2024',0),
('20250102','AIF23922','A',1,'2023/2024',0),
('20250103','AIF23923','A',2,'2023/2024',0),
('20250103','AIF23923','B',2,'2023/2024',0),
('20250102','AIF23924','A',2,'2023/2024',0),
('20250102','AIF23924','B',2,'2023/2024',0),
('20250101','AIF23925','A',1,'2023/2024',0),
('20250101','AIF23925','B',1,'2023/2024',0);

-- ===========================
-- MATA KULIAH MAHASISWA ARCHIVE (unik)
-- ===========================
INSERT INTO mata_kuliah_mahasiswa (id_user, kode_mk, kelas, semester, tahun_akademik, is_active) VALUES
-- AIF23921
('6182301001','AIF23921','A',1,'2023/2024',0),
('6182301002','AIF23921','A',1,'2023/2024',0),
('6182301003','AIF23921','B',1,'2023/2024',0),
('6182301004','AIF23921','B',1,'2023/2024',0),
-- AIF23922
('6182301005','AIF23922','A',1,'2023/2024',0),
('6182301006','AIF23922','A',1,'2023/2024',0),
('6182301007','AIF23922','A',1,'2023/2024',0),
-- AIF23923
('6182301008','AIF23923','A',2,'2023/2024',0),
('6182301009','AIF23923','A',2,'2023/2024',0),
('6182301010','AIF23923','B',2,'2023/2024',0),
('6182301011','AIF23923','B',2,'2023/2024',0),
-- AIF23924
('6182301012','AIF23924','A',2,'2023/2024',0),
('6182301013','AIF23924','B',2,'2023/2024',0),
-- AIF23925
('6182301014','AIF23925','A',1,'2023/2024',0),
('6182301015','AIF23925','B',1,'2023/2024',0);

-- ===========================
-- TUGAS BESAR
-- ===========================
INSERT INTO tugas_besar (id_tugas, id_user, id_rubrik, kode_mk, judul_tugas, deskripsi, deadline, status, mode_kel, min_anggota, max_anggota, is_active) VALUES
(101,'20250101',101,'AIF23921','TB Logika Proposisi','Analisis logika proposisi','2023-10-15 23:59:00','Finalized','Dosen',2,3,0),
(102,'20250101',102,'AIF23921','TB Graf dan Pohon','Implementasi graf dan pohon','2023-11-20 23:59:00','Finalized','Mahasiswa',2,4,0),
(103,'20250102',103,'AIF23922','TB Limit dan Kontinuitas','Analisis limit dan kontinuitas','2023-10-20 23:59:00','Finalized','Mahasiswa',2,3,0),
(104,'20250102',104,'AIF23922','TB Integral Tentu','Penerapan integral','2023-12-05 23:59:00','Finalized','Dosen',2,4,0),
(105,'20250103',105,'AIF23923','TB Studi Kasus Etika','Analisis kasus etika','2023-09-25 23:59:00','Finalized','Mahasiswa',2,3,0),
(106,'20250103',106,'AIF23923','TB Kode Etik Profesional','Pedoman kode etik','2023-11-10 23:59:00','Finalized','Dosen',1,2,0),
(107,'20250102',107,'AIF23924','TB Evolusi Komputer','Perkembangan komputer','2023-09-30 23:59:00','Finalized','Mahasiswa',2,3,0),
(108,'20250102',108,'AIF23924','TB Tokoh Teknologi','Kontribusi tokoh TI','2023-11-15 23:59:00','Finalized','Dosen',2,3,0),
(109,'20250101',109,'AIF23925','TB Penulisan Laporan Teknis','Laporan teknis bahasa Indonesia','2023-10-10 23:59:00','Finalized','Dosen',2,3,0),
(110,'20250101',110,'AIF23925','TB Proposal Proyek','Proposal proyek IT','2023-11-25 23:59:00','Finalized','Mahasiswa',2,4,0);

-- ===========================
-- KELOMPOK
-- ===========================
INSERT INTO kelompok (id_kelompok, nama_kelompok) VALUES
(101,'Kelompok Logic 1'),(102,'Kelompok Logic 2'),(103,'Kelompok Graf 1'),
(104,'Kelompok Limit 1'),(105,'Kelompok Integral 1'),
(106,'Kelompok Etika 1'),(107,'Kelompok Kode Etik 1'),
(108,'Kelompok Evolusi 1'),(109,'Kelompok Tokoh 1'),
(110,'Kelompok Laporan 1'),(111,'Kelompok Proposal 1');

-- ===========================
-- RELASI TUGAS - KELOMPOK
-- ===========================
INSERT INTO tugas_besar_kelompok (id_kelompok, id_tugas) VALUES
(101,101),(102,102),(103,102),(104,103),(105,104),(106,105),(107,106),(108,107),(109,108),(110,109),(111,110);

-- ===========================
-- ANGGOTA KELOMPOK
-- ===========================
INSERT INTO user_kelompok (id_user, id_kelompok, role, is_active) VALUES
('6182301001',101,'leader',0),('6182301002',101,'member',0),
('6182301003',102,'leader',0),('6182301004',102,'member',0),
('6182301005',103,'leader',0),('6182301006',103,'member',0),
('6182301007',104,'leader',0),('6182301008',104,'member',0),
('6182301009',105,'leader',0),('6182301010',105,'member',0),
('6182301011',106,'leader',0),('6182301012',106,'member',0),
('6182301013',107,'leader',0),('6182301014',107,'member',0),
('6182301015',108,'leader',0),('6182301016',108,'member',0),
('6182301017',109,'leader',0),('6182301018',109,'member',0),
('6182301019',110,'leader',0),('6182301020',110,'member',0),
('6182301021',111,'leader',0),('6182301022',111,'member',0);

-- ===========================
-- NILAI
-- ===========================
INSERT INTO nilai (id_user, id_tugas, nilai_pribadi, nilai_kelompok) VALUES
-- Tugas 101: TB Logika Proposisi
('6182301001',101,85,88),
('6182301002',101,82,88),
('6182301003',101,90,87),

-- Tugas 102: TB Graf dan Pohon
('6182301004',102,88,85),
('6182301005',102,86,87),
('6182301006',102,85,88),

-- Tugas 103: TB Limit dan Kontinuitas
('6182301007',103,92,90),
('6182301008',103,89,88),
('6182301009',103,80,82),

-- Tugas 104: TB Integral Tentu
('6182301010',104,88,85),
('6182301011',104,91,92),
('6182301012',104,87,85),

-- Tugas 105: TB Studi Kasus Etika
('6182301013',105,90,89),
('6182301014',105,85,87),
('6182301015',105,88,89),

-- Tugas 106: TB Kode Etik Profesional
('6182301016',106,92,90),
('6182301017',106,85,85),
('6182301018',106,89,88),

-- Tugas 107: TB Evolusi Komputer
('6182301019',107,87,86),
('6182301020',107,90,91),
('6182301021',107,84,85),

-- Tugas 108: TB Tokoh Teknologi
('6182301022',108,88,89),
('6182301023',108,92,90),
('6182301024',108,85,87),

-- Tugas 109: TB Penulisan Laporan Teknis
('6182301001',109,90,88),
('6182301002',109,86,85),
('6182301003',109,88,90),

-- Tugas 110: TB Proposal Proyek
('6182301004',110,91,92),
('6182301005',110,89,90),
('6182301006',110,85,88);

-- ===========================
-- KOMPONEN NILAI
-- ===========================
INSERT INTO komponen_nilai (id_rubrik, nama_komponen, bobot, catatan, jam, tanggal) VALUES
(101,'Analisis Logika',35,'Analisis proposisi','09:00:00','2023-10-12'),
(102,'Implementasi Graf',40,'Pembuatan graf','10:00:00','2023-11-15'),
(103,'Limit Kasus',25,'Analisis limit','11:00:00','2023-10-18'),
(104,'Integral Tugas',35,'Integral terapan','09:00:00','2023-12-03'),
(105,'Kasus Etika',40,'Analisis kasus','13:00:00','2023-09-23'),
(106,'Kode Etik',30,'Pedoman etik','10:00:00','2023-11-08'),
(107,'Evolusi Komputer',35,'Sejarah komputer','14:00:00','2023-09-28'),
(108,'Tokoh TI',25,'Analisis tokoh','09:00:00','2023-11-13'),
(109,'Laporan Teknis',40,'Penulisan laporan','10:00:00','2023-10-08'),
(110,'Proposal Proyek',35,'Proposal proyek IT','13:00:00','2023-11-23');

-- ===========================
-- JADWAL PENILAIAN
-- ===========================
INSERT INTO jadwal_penilaian (id_rubrik, tanggal, jam, ruangan) VALUES
(101,'2023-10-12','09:00:00','R201'),
(102,'2023-11-15','10:00:00','Lab 2'),
(103,'2023-10-18','11:00:00','R202'),
(104,'2023-12-03','09:00:00','R203'),
(105,'2023-09-23','13:00:00','R204'),
(106,'2023-11-08','10:00:00','R205'),
(107,'2023-09-28','14:00:00','R206'),
(108,'2023-11-13','09:00:00','R207'),
(109,'2023-10-08','10:00:00','R208'),
(110,'2023-11-23','13:00:00','R209');