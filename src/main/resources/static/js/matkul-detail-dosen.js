document.addEventListener("DOMContentLoaded", () => {

    const mkTabDiv = document.querySelector('.mk-tab');
    const mataKuliahId = mkTabDiv ? mkTabDiv.getAttribute('data-mk-kode') : null;
    
    const tabButtons = document.querySelectorAll('.mk-tab .tab');

    tabButtons.forEach(button => {
        if (button.hasAttribute('data-target-url')) {
            button.addEventListener('click', function() {
                const url = this.getAttribute('data-target-url');
                if (url) {
                    window.location.href = url;
                }
            });
        }
    });

    const handleLogout = () => {
        fetch('/logout', { method: 'POST' }) 
            .finally(() => {
                 window.location.href = '/'; 
            });
    };
    const logoutButton = document.getElementById('logoutButton'); 
    if (logoutButton) {
        console.log("DEBUG: Listener dipasang pada tombol Logout.");
        logoutButton.addEventListener('click', handleLogout);
    } 

    const toggleButton = document.getElementById('toggle-tambah-tugas'); 
    const listTugasView = document.getElementById('list-tugas-view');
    const buatTugasView = document.getElementById('buat-tugas-view');
    const breadcrumb = document.querySelector('.breadcrumb');

    if (toggleButton && listTugasView && buatTugasView) { 
        
        console.log("DEBUG: Listener dipasang pada tombol Tambah Tugas.");
        
        let isListView = true;
        const mkTitleElement = document.getElementById('mk-title');
        const originalBreadcrumb = breadcrumb ? breadcrumb.innerHTML : '';
        
        listTugasView.style.display = 'block';
        buatTugasView.style.display = 'none';

        const toggleView = () => {
            isListView = !isListView;
            
            if (isListView) {

                listTugasView.style.display = 'block';
                buatTugasView.style.display = 'none';
                if (breadcrumb) {
                     breadcrumb.innerHTML = originalBreadcrumb; 
                }
            } else {

                listTugasView.style.display = 'none';
                buatTugasView.style.display = 'block';
                if (breadcrumb) {
                    const mkTitle = mkTitleElement ? mkTitleElement.textContent : 'Mata Kuliah';
                    breadcrumb.innerHTML = `Daftar Mata Kuliah > ${mkTitle} > Tambah Tugas`;
                }
            }
        };

        toggleButton.addEventListener('click', toggleView);
    }
    
    const tugasForm = document.getElementById('tugas-form');
    if (tugasForm && mataKuliahId) {
        const url = `/api/dosen/matakuliah/${mataKuliahId}/tugas`; 
        
        tugasForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            
            const namaTugas = document.getElementById('nama-tugas').value;
            const deadline = document.getElementById('deadline').value;
            const penentuanAnggota = document.getElementById('penentuan-anggota').value;
            const deskripsi = document.getElementById('deskripsi-tugas').value;
            
            if (!namaTugas || !deadline || !deskripsi || (penentuanAnggota === "")){
                alert("Harap lengkapi semua field tugas!");
                return;
            }

            const data = {
                judulTugas: namaTugas, 
                deadline: deadline, 
                modeKel: penentuanAnggota, 
                deskripsi: deskripsi
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
    
});