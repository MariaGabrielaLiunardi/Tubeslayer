# Fitur Pemberian Nilai oleh Dosen - Dokumentasi

## Overview
Fitur pemberian nilai memungkinkan dosen untuk memberikan penilaian kepada mahasiswa secara komprehensif dengan dukungan:
- Penilaian per komponen yang sudah didefinisikan dalam rubrik
- Validasi nilai (0-100)
- Otomatis perhitungan nilai kelompok berdasarkan bobot komponen
- Opsi untuk menyamakan nilai untuk semua anggota kelompok
- Penyimpanan dan pengambilan data nilai dari database

---

## Komponen yang Dibuat/Dimodifikasi

### 1. **Entity & Database**

#### `Nilai.java` (DIMODIFIKASI)
- Ditambahkan relasi `OneToMany` dengan `NilaiKomponen`
- Menyimpan nilai pribadi dan nilai kelompok

#### `NilaiKomponen.java` (BARU)
- Entity untuk menyimpan nilai per komponen untuk setiap user
- Fields: `id_nilai_komponen`, `id_nilai`, `id_komponen`, `nilai_komponen`
- Memungkinkan tracking detail nilai per komponen

#### Database Schema Update
```sql
CREATE TABLE nilai_komponen (
    id_nilai_komponen INT AUTO_INCREMENT PRIMARY KEY,
    id_nilai INT NOT NULL,
    id_komponen INT NOT NULL,
    nilai_komponen INT NOT NULL,
    UNIQUE KEY uk_nilai_komponen (id_nilai, id_komponen),
    FOREIGN KEY (id_nilai) REFERENCES nilai(id_nilai),
    FOREIGN KEY (id_komponen) REFERENCES komponen_nilai(id_komponen)
);
```

---

### 2. **Repository**

#### `NilaiRepository.java` (ENHANCED)
Tambahan custom queries:
- `findByUser_IdUserAndTugas_IdTugas()` - Cari nilai user untuk tugas tertentu
- `findByTugas_IdTugas()` - Cari semua nilai untuk satu tugas
- `findByUser_IdUser()` - Cari semua nilai untuk satu user
- `existsByUser_IdUserAndTugas_IdTugas()` - Check apakah nilai sudah ada
- `findByTugasAndKelompok()` - Cari nilai untuk kelompok dan tugas tertentu

#### `NilaiKomponenRepository.java` (BARU)
- `findByNilai_IdNilaiAndKomponen_IdKomponen()` - Cari nilai komponen spesifik
- `findByNilai_IdNilai()` - Cari semua nilai komponen untuk satu nilai
- `findByKomponen_IdKomponen()` - Cari semua nilai untuk satu komponen

#### `UserKelompokRepository.java` (ENHANCED)
- `findByUser_IdUserAndKelompok_InTugaBesar()` - Cari kelompok user dalam tugas

---

### 3. **Service**

#### `NilaiService.java` (BARU)
Menangani semua business logic pemberian nilai:

**Method Utama:**
- `simpanNilai(idUser, idTugas, nilaiPerKomponen, isSamaBuat)` 
  - Menyimpan nilai user untuk tugas dengan validasi
  - Otomatis menghitung nilai kelompok
  - Opsi untuk menerapkan nilai sama ke seluruh anggota kelompok
  
- `hitungNilaiKelompok(rubrik, nilaiPerKomponen)`
  - Rumus: `Nilai Kelompok = Sum(nilai_komponen_i * bobot_i / 100)`
  - Validasi bahwa semua komponen tersedia
  
- `getNilaiByUserAndTugas(idUser, idTugas)` - Ambil nilai yang sudah ada
- `getNilaiKomponenByNilai(idNilai)` - Ambil detail nilai per komponen
- `hapusNilai(idUser, idTugas)` - Hapus nilai dengan cascade delete
- `isSemuaKomponenTerisi(idTugas, nilaiPerKomponen)` - Validasi kelengkapan

**Validasi:**
- Nilai harus antara 0-100
- Semua komponen harus memiliki nilai
- User dan tugas harus ada di database

---

### 4. **DTO**

#### `PemberianNilaiPerKomponenDTO.java` (BARU)
Digunakan untuk menerima request pemberian nilai:
```java
{
    "idUser": "user_id",
    "idTugas": 1,
    "nilaiPerKomponen": {
        1: 80,     // id_komponen: nilai
        2: 75,
        3: 90
    },
    "isSamaBuat": false  // terapkan ke semua anggota?
}
```

---

### 5. **Controller**

#### `DosenController.java` (ENHANCED)

**Endpoint GET:**

1. `/dosen/pemberian-nilai` 
   - Query params: `idTugas` (required), `idKelompok` (optional)
   - Menampilkan form pemberian nilai
   - Menampilkan daftar anggota kelompok
   - Menampilkan rubrik dan komponen
   - Memuat nilai yang sudah ada

2. `/api/nilai/{idUser}/{idTugas}`
   - Mengambil nilai yang sudah ada untuk user dan tugas
   - Response termasuk nilai per komponen

**Endpoint POST:**

1. `/api/nilai/simpan` (RequestBody: PemberianNilaiPerKomponenDTO)
   - Simpan nilai untuk satu user
   - Validasi komplet semua input
   - Jika `isSamaBuat=true`, otomatis terapkan ke semua anggota kelompok
   - Response: `{success, message, idNilai, nilaiKelompok}`

2. `/api/nilai/hapus` (QueryParams: idUser, idTugas)
   - Hapus nilai dan semua nilai komponen terkait
   - Response: `{success, message}`

**Security:**
- Hanya dosen pembuat tugas yang bisa memberikan/mengubah nilai
- Authorization check di setiap endpoint

---

### 6. **Template HTML**

#### `pemberian-nilai.html` (BARU)
Modern responsive interface dengan fitur:

**UI Components:**
- Back button untuk kembali ke detail tugas
- Tugas header dengan info (judul, deskripsi, mata kuliah)
- Kelompok selector untuk memilih kelompok yang akan dinilai
- Anggota list dengan:
  - Nama, ID, dan role (leader/member)
  - Input form untuk setiap komponen (0-100)
  - Real-time preview nilai kelompok (terhitung otomatis)
  - Checkbox "Sama Buat" untuk apply nilai ke semua anggota
  - Tombol Simpan dan Hapus

**JavaScript Features:**
- Real-time calculation of nilai kelompok menggunakan rumus bobot
- Input validation (0-100)
- Error highlighting
- Fetch API untuk komunikasi dengan backend
- Loading state handling
- Success/error notifications

---

### 7. **CSS**

#### `pemberian-nilai-dosen.css` (ENHANCED)
Styling modern dengan:
- Gradient header
- Card-based layout untuk setiap anggota
- Responsive design (desktop, tablet, mobile)
- Hover effects dan transitions
- Color-coded buttons (green untuk simpan, red untuk hapus)
- Error state styling

---

## Flow Pemberian Nilai

### 1. **Akses Halaman**
```
Dosen mengakses: /dosen/pemberian-nilai?idTugas=X&idKelompok=Y
```

### 2. **Tampilkan Data**
- Load rubrik dan komponen dari tugas
- Load daftar kelompok untuk tugas
- Jika kelompok dipilih:
  - Load daftar anggota kelompok
  - Load nilai yang sudah ada (jika ada)

### 3. **Input Nilai**
- Dosen mengisi nilai untuk setiap komponen (0-100)
- JavaScript auto-calculate nilai kelompok: Sum(nilai * bobot / 100)
- Optional: Check "Sama Buat" untuk apply ke semua anggota

### 4. **Simpan Nilai**
```javascript
POST /api/nilai/simpan
{
    "idUser": "M001",
    "idTugas": 5,
    "nilaiPerKomponen": {1: 80, 2: 75, 3: 90},
    "isSamaBuat": false
}
```

Service akan:
1. Validasi input (0-100, semua komponen ada)
2. Cek user dan tugas exist
3. Hitung nilai kelompok
4. Simpan `Nilai` record
5. Simpan setiap `NilaiKomponen` record
6. Jika `isSamaBuat=true`, loop anggota kelompok dan apply nilai sama

### 5. **Tampilkan Ulang**
- Nilai yang disimpan langsung ter-display
- Tombol Hapus menjadi active
- Alert success message

---

## Validasi & Error Handling

### Validasi Input
- ✅ Nilai harus 0-100
- ✅ Semua komponen harus terisi
- ✅ User harus exist di database
- ✅ Tugas harus exist di database
- ✅ Hanya dosen pembuat tugas yang bisa beri nilai

### Error Response
```javascript
{
    "error": "Pesan error deskriptif"
}
// atau
{
    "success": false,
    "message": "Detail error"
}
```

---

## Rumus Perhitungan Nilai Kelompok

$$\text{Nilai Kelompok} = \sum_{i=1}^{n} \left( \text{Nilai}_i \times \frac{\text{Bobot}_i}{100} \right)$$

**Contoh:**
- Komponen A: Nilai 80, Bobot 40% → 80 × 0.4 = 32
- Komponen B: Nilai 75, Bobot 30% → 75 × 0.3 = 22.5
- Komponen C: Nilai 90, Bobot 30% → 90 × 0.3 = 27
- **Total Nilai Kelompok: 32 + 22.5 + 27 = 81.5 ≈ 82 (dibulatkan)**

---

## Testing

### Test Case 1: Simpan Nilai Satu User
```bash
POST /api/nilai/simpan
{
    "idUser": "M001",
    "idTugas": 1,
    "nilaiPerKomponen": {1: 85, 2: 80, 3: 75},
    "isSamaBuat": false
}
# Expected: Success, nilai_kelompok = (85*40 + 80*30 + 75*30) / 100 = 80
```

### Test Case 2: Sama Buat untuk Kelompok
```bash
POST /api/nilai/simpan
{
    "idUser": "M001",
    "idTugas": 1,
    "nilaiPerKomponen": {1: 85, 2: 80, 3: 75},
    "isSamaBuat": true
}
# Expected: Nilai disimpan untuk M001 dan semua anggota kelompok M001
```

### Test Case 3: Ambil Nilai yang Sudah Ada
```bash
GET /api/nilai/M001/1
# Expected: 
# {
#     "exists": true,
#     "idNilai": 5,
#     "nilaiKelompok": 80,
#     "nilaiPribadi": 80,
#     "nilaiPerKomponen": {1: 85, 2: 80, 3: 75}
# }
```

### Test Case 4: Hapus Nilai
```bash
POST /api/nilai/hapus?idUser=M001&idTugas=1
# Expected: Success, nilai dan nilai_komponen dihapus
```

---

## Integrasi dengan Halaman Lain

### Dari Detail Tugas ke Pemberian Nilai
Tambahkan tombol di `hlmtubes-dosen.html`:
```html
<a href="/dosen/pemberian-nilai?idTugas=${tugas.idTugas}">
    Berikan Nilai →
</a>
```

### Dari Dashboard Dosen
Tampilkan ringkasan tugas yang sudah dinilai vs belum.

---

## Future Enhancements

1. **Batch Upload Nilai** - Import dari CSV/Excel
2. **History Nilai** - Track perubahan nilai
3. **Komentar Feedback** - Dosen bisa kasih feedback per komponen
4. **Export Nilai** - Download dalam format PDF/Excel
5. **Notifikasi** - Email ke mahasiswa saat nilai published
6. **Rubrik Fleksibel** - Dosen bisa update rubrik sebelum beri nilai
7. **Curve Grading** - Kurva nilai otomatis

---

## File yang Terubah/Dibuat

### Baru:
- `entity/NilaiKomponen.java`
- `dto/PemberianNilaiPerKomponenDTO.java`
- `repository/NilaiKomponenRepository.java`
- `service/NilaiService.java`
- `templates/dosen/pemberian-nilai.html`

### Modified:
- `entity/Nilai.java` - Add relation to NilaiKomponen
- `repository/NilaiRepository.java` - Add custom queries
- `repository/UserKelompokRepository.java` - Add findByUser_IdUserAndKelompok_InTugaBesar()
- `controller/DosenController.java` - Add pemberian nilai endpoints
- `resources/db/create_tables.sql` - Add nilai_komponen table
- `static/css/pemberian-nilai-dosen.css` - Enhanced styling

---

**Versi:** 1.0  
**Tanggal:** 13 Desember 2025  
**Status:** Ready for Testing
