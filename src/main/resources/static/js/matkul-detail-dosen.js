document.addEventListener("DOMContentLoaded", () => {

    const basePath = '/dosen'; 
    const mkTabDiv = document.querySelector('.mk-tab');
    const mataKuliahId = mkTabDiv ? mkTabDiv.getAttribute('data-mk-kode') : null;
    
    if (!mataKuliahId) {
        console.error("Kode Mata Kuliah tidak ditemukan.");
    }

    // Tambah Tugas

    const listTugasView = document.getElementById('list-tugas-view');
    const buatTugasView = document.getElementById('buat-tugas-view');
    const toggleButton = document.getElementById('toggle-tambah-tugas'); 
    const breadcrumb = document.querySelector('.breadcrumb');
    
    let isListView = true;
    const originalBreadcrumb = breadcrumb ? breadcrumb.innerHTML : '';


    if (toggleButton && buatTugasView) { 
        
        const resetToListView = () => {
            if (!isListView) {
                isListView = true;
                if (listTugasView) listTugasView.style.display = 'block';
                if (buatTugasView) buatTugasView.style.display = 'none';
                if (breadcrumb) {
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
                if (breadcrumb) breadcrumb.innerHTML = originalBreadcrumb + ` > Tambah Tugas`;
            }
        };

        toggleButton.addEventListener('click', toggleView);
    }
    
    // Submit Form Tambah Tugas
    
    const tugasForm = document.getElementById('tugas-form');

    if (tugasForm && mataKuliahId) {
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
                judulTugas: namaTugas, 
                deadline: deadline, 
                deskripsi: deskripsi,
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

    // Logout

    const handleLogout = () => {
        console.log("Melakukan proses logout..."); 
        fetch('/logout', { method: 'POST' }) 
            .then(() => {
                 // redirect ke halaman utama
                 window.location.href = '/'; 
            })
            .catch(error => {
                 console.error("Logout gagal:", error);
                 // tetap redirect meskipun fetch gagal
                 window.location.href = '/'; 
            });
    };

    const logoutButton = document.getElementById('logoutButton'); 
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    } 
    
});