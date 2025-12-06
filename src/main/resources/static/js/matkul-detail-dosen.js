document.addEventListener("DOMContentLoaded", () => {
    
    // Asumsi kita tahu base path adalah '/dosen' di sini
    const basePath = '/dosen'; 
    
    // Ambil kodeMK dari elemen header tab (atau dari form)
    const mkTabDiv = document.querySelector('.mk-tab');
    const mataKuliahId = mkTabDiv ? mkTabDiv.getAttribute('data-mk-kode') : null;
    
    if (!mataKuliahId) {
        console.error("Kode Mata Kuliah tidak ditemukan.");
    }
    
    // --- Logika Tab Navigation (sekarang handled by th:onclick di HTML) ---
    // Logika di sini sekarang hanya perlu fokus pada tampilan di halaman ini 
    // jika ada tab lain selain "Peserta" yang memicu pengalihan.
    // Karena kita menggunakan th:onclick untuk Daftar Peserta, logika ini bisa dihapus/disederhanakan.

    // ------------------------------------------------------------------
    // --- Logika Form Toggle (Dosen) ---
    // ------------------------------------------------------------------

    const listTugasView = document.getElementById('list-tugas-view');
    const buatTugasView = document.getElementById('buat-tugas-view');
    const toggleButton = document.getElementById('toggle-tambah-tugas'); 
    const breadcrumb = document.querySelector('.breadcrumb');
    
    let isListView = true;
    const originalBreadcrumb = breadcrumb ? breadcrumb.innerHTML : '';
    
    // Fungsionalitas Dosen (Form Toggle)
    if (toggleButton && buatTugasView) { 
        
        const resetToListView = () => {
            if (!isListView) {
                isListView = true;
                if (listTugasView) listTugasView.style.display = 'block';
                if (buatTugasView) buatTugasView.style.display = 'none';
                if (breadcrumb) {
                     // Hilangkan teks tambahan "Tambah Tugas"
                     breadcrumb.innerHTML = originalBreadcrumb.split(' > ')[0]; 
                }
            }
        };

        const toggleView = () => {
            isListView = !isListView;
            
            if (isListView) {
                resetToListView();
            } else {
                if (listTugasView) listTugasView.style.display = 'none';
                if (buatTugasView) buatTugasView.style.display = 'block';
                // Tambahkan teks tambahan "Tambah Tugas" ke breadcrumb
                if (breadcrumb) breadcrumb.innerHTML = originalBreadcrumb + ` > <b>Tambah Tugas</b>`;
            }
        };

        toggleButton.addEventListener('click', toggleView);
    }
    
    // ------------------------------------------------------------------
    // --- SUBMIT FORM TUGAS (INTEGRASI API) ---
    // ------------------------------------------------------------------
    
    const tugasForm = document.getElementById('tugas-form');

    if (tugasForm && mataKuliahId) {
        // Endpoint yang menggunakan kodeMK sebagai path variable
        const url = `/api/dosen/matakuliah/${mataKuliahId}/tugas`; 
        
        tugasForm.addEventListener('submit', async (event) => {
            event.preventDefault(); 
            
            const namaTugas = document.getElementById('nama-tugas').value;
            const deadline = document.getElementById('deadline').value;
            const deskripsi = document.getElementById('deskripsi-tugas').value;
            
            // Logika validasi dan fetch API tetap sama
            if (!namaTugas || !deadline || !deskripsi) {
                alert("Harap lengkapi semua field tugas!");
                return;
            }

            const data = {
                judulTugas: namaTugas, // Sesuaikan dengan nama field di Entitas TugasBesar
                deadline: deadline, 
                deskripsi: deskripsi,
                // Anda mungkin perlu menambahkan status, mode_kel, min_anggota, max_anggota, dll.
                // Jika data ini dihardcode atau diambil dari input tersembunyi.
            };

            try {
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(data),
                });

                if (response.ok) {
                    alert(`Tugas "${namaTugas}" berhasil ditambahkan!`);
                    tugasForm.reset();
                    // Redirect kembali ke list view atau reload
                    window.location.reload(); 
                } else {
                    const error = await response.json();
                    alert(`Gagal menambahkan tugas: ${error.message || response.statusText}`);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('Terjadi kesalahan koneksi saat menambahkan tugas.');
            }
        });
    }

    // --- Logika Logout (disederhanakan, ambil dari file dash-user.js jika ada) ---
    // Asumsi logika logout ditangani oleh dash-user.js atau disematkan di sini.
    // File: matkul-detail-dosen.js

// ... (kode lainnya) ...

    // --- Logika Logout ---
    const handleLogout = () => {
        console.log("Melakukan proses logout..."); 
        // Menggunakan fetch POST untuk memicu logout Spring Security
        fetch('/logout', { method: 'POST' }) 
            .then(() => {
                 // Setelah berhasil, redirect ke halaman utama
                 window.location.href = '/'; 
            })
            .catch(error => {
                 console.error("Logout gagal:", error);
                 // Fallback: tetap redirect meskipun fetch gagal
                 window.location.href = '/'; 
            });
    };

    const logoutButton = document.getElementById('logoutButton'); 
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    } 
    
// ... (kode lainnya) ...
});