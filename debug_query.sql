USE tubeslayer;

-- Check all mata_kuliah_dosen entries
SELECT 'All mata_kuliah_dosen entries:' as info;
SELECT id_user, kode_mk, tahun_akademik, is_active, kelas FROM mata_kuliah_dosen;

-- Check for dosen 20250101 (Agus Santoso)
SELECT 'Data for dosen 20250101:' as info;
SELECT id_user, kode_mk, tahun_akademik, is_active, kelas 
FROM mata_kuliah_dosen 
WHERE id_user = '20250101';

-- Check for dosen 20250101 in tahun_akademik 2025/2026
SELECT 'Data for dosen 20250101 in 2025/2026:' as info;
SELECT md.id_user, md.kode_mk, md.tahun_akademik, md.is_active, md.kelas, mk.nama
FROM mata_kuliah_dosen md
JOIN mata_kuliah mk ON md.kode_mk = mk.kode_mk
WHERE md.id_user = '20250101' AND md.tahun_akademik = '2025/2026' AND md.is_active = true;

-- Check all users
SELECT 'All users:' as info;
SELECT id_user, nama, role FROM user_table LIMIT 20;
