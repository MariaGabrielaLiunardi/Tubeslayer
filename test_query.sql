-- Test query untuk mata_kuliah_dosen
SELECT * FROM mata_kuliah_dosen WHERE id_user = '20250102' AND is_active = 1;

-- Test untuk melihat semua data
SELECT md.id_user, md.kode_mk, md.is_active, md.tahun_akademik, mk.nama 
FROM mata_kuliah_dosen md 
LEFT JOIN mata_kuliah mk ON md.kode_mk = mk.kode_mk
LIMIT 20;

-- Cek berapa total record
SELECT COUNT(*) FROM mata_kuliah_dosen;
