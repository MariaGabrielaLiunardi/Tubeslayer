document.addEventListener("DOMContentLoaded", () => {
    // Tentukan role/base path berdasarkan URL yang sedang diakses
    const currentPath = window.location.pathname;
    const basePath = currentPath.includes('/dosen/') ? '/dosen' : '/mahasiswa';
    
    // Asumsi ID Mata Kuliah didapatkan dari atribut data di form
    const tugasForm = document.getElementById('tugas-form');
    const mataKuliahId = tugasForm ? tugasForm.getAttribute('data-mk-id') : null; 

    // --- Logika Tab Navigation ---
    const tabs = document.querySelectorAll('.mk-tab button');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const tabName = tab.textContent;

            if (tabName === 'Daftar Peserta') {
                // Navigasi ke halaman peserta, mungkin perlu ID MK
                window.location.href = basePath + '/matkul-peserta'; 
            } else if (tabName === 'Kuliah') {
                // Refresh/Tetap di halaman detail
                window.location.href = 'matkul-detail.html';
            }
            // Tambahkan logika untuk Tab Nilai di sini
        });
    });

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
                if (breadcrumb) breadcrumb.innerHTML = originalBreadcrumb;
            }
        };

        const toggleView = () => {
            isListView = !isListView;
            
            if (isListView) {
                resetToListView();
            } else {
                if (listTugasView) listTugasView.style.display = 'none';
                if (buatTugasView) buatTugasView.style.display = 'block';
                if (breadcrumb) breadcrumb.innerHTML = originalBreadcrumb + ` > <b>Tambah Tugas</b>`;
            }
        };

        toggleButton.addEventListener('click', toggleView);
    }
    
    // ------------------------------------------------------------------
    // --- SUBMIT FORM TUGAS (INTEGRASI API) ---
    // ------------------------------------------------------------------
    
    if (tugasForm && mataKuliahId) {
        const url = `/api/dosen/matakuliah/${mataKuliahId}/tugas`; // Contoh API endpoint
        
        tugasForm.addEventListener('submit', async (event) => {
            event.preventDefault(); 
            
            const namaTugas = document.getElementById('nama-tugas').value;
            const deadline = document.getElementById('deadline').value;
            const deskripsi = document.getElementById('deskripsi-tugas').value;

            if (!namaTugas || !deadline || !deskripsi) {
                alert("Harap lengkapi semua field tugas!");
                return;
            }

            const data = {
                nama: namaTugas,
                deadline: deadline, // Format YYYY-MM-DD
                deskripsi: deskripsi,
                // Tambahkan field lain yang mungkin dibutuhkan oleh backend (misalnya dosenId)
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
                    // Redirect atau reload halaman untuk menampilkan tugas baru
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


    // ------------------------------------------------------------------
    // --- LOGIKA LOGOUT ---
    // ------------------------------------------------------------------
    
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
    
});