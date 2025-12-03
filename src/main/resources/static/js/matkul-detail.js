document.addEventListener("DOMContentLoaded", () => {
    // Tentukan role/base path berdasarkan URL yang sedang diakses
    // Contoh sederhana: Cek apakah URL mengandung '/dosen/' atau '/mahasiswa/'
    const currentPath = window.location.pathname;
    const basePath = currentPath.includes('/dosen/') ? '/dosen' : '/mahasiswa';

    // --- Logika Tab Navigation ---
    const tabs = document.querySelectorAll('.mk-tab button');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const tabName = tab.textContent;

            // Navigasi menggunakan basePath yang benar
            if (tabName === 'Daftar Peserta') {
                window.location.href = basePath + '/matkul-peserta';
            } else if (tabName === 'Kuliah') {
                window.location.href = basePath + '/matkul-detail';
            }
        });
    });

    // --- Logika Halaman Detail (Form Toggle & Submit Tugas) ---
    // Elemen yang mungkin tidak ada di Mahasiswa (dikelola oleh if/else)

    const listTugasView = document.getElementById('list-tugas-view');
    const buatTugasView = document.getElementById('buat-tugas-view');
    const toggleButton = document.getElementById('toggle-tambah-tugas'); // Tombol yang hanya ada di Dosen
    const breadcrumb = document.querySelector('.breadcrumb');
    const tugasForm = document.getElementById('tugas-form');
    
    // ... (Container tugas lainnya) ...
    const tugasBesarList = document.querySelector('.tugas-besar-list');
    const tugasMendatangList = document.querySelector('.tugas-mendatang-list');
    const tugasKosongMendatang = document.getElementById('tugas-mendatang-kosong');
    const tugasKosongBesar = document.getElementById('tugas-besar-kosong');
    
    let isListView = true;
    const originalBreadcrumb = breadcrumb.innerHTML;

    // Fungsionalitas Dosen
    if (toggleButton && buatTugasView) { // Hanya aktif jika elemen Dosen ada
        
        const resetToListView = () => {
            if (!isListView) {
                isListView = true;
                listTugasView.style.display = 'block';
                buatTugasView.style.display = 'none';
                breadcrumb.innerHTML = originalBreadcrumb;
            }
        };

        const toggleView = () => {
            isListView = !isListView;
            
            if (isListView) {
                resetToListView();
            } else {
                listTugasView.style.display = 'none';
                buatTugasView.style.display = 'block';
                breadcrumb.innerHTML = originalBreadcrumb + ` > <b>Tambah Tugas</b>`;
            }
        };

        // Event listener untuk tombol Tambah Tugas
        toggleButton.addEventListener('click', toggleView);
        
        // --- SUBMIT FORM TUGAS (hanya untuk dosen) ---
        if (tugasForm) {
            tugasForm.addEventListener('submit', (event) => {
                event.preventDefault(); 
                
                // ... (Logika penambahan tugas sama seperti sebelumnya) ...
                const namaTugas = document.getElementById('nama-tugas').value;
                const deadline = document.getElementById('deadline').value;
                let deskripsi = document.getElementById('deskripsi-tugas').value;

                if (namaTugas && deadline && deskripsi) {
                    
                    const dateObj = new Date(deadline);
                    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
                    const formattedDeadline = dateObj.toLocaleDateString('id-ID', options);
                    const previewDeskripsi = deskripsi.substring(0, 150) + (deskripsi.length > 150 ? '...' : '');
                    
                    const newTugasBesar = document.createElement('div');
                    newTugasBesar.classList.add('tugas-besar');
                    newTugasBesar.innerHTML = `
                        <div class="tugas-header">
                            <img src="/icon/blue-folder.png" class="folder-icon">
                            <h3>${namaTugas}</h3>
                        </div>
                        <p>${previewDeskripsi}</p>
                        <button class="btn-lihat">Lihat Tugas ></button>`;
                    
                    if (tugasBesarList) {
                        if (tugasKosongBesar) { tugasKosongBesar.remove(); }
                        tugasBesarList.appendChild(newTugasBesar);
                    }
                    
                    const newTugasMendatang = document.createElement('div');
                    newTugasMendatang.classList.add('tugas-mendatang-card');
                    newTugasMendatang.innerHTML = `
                        <div class="deadline-info">
                            <img src="/icon/kalender.png" class="kalender-icon" alt="kalender">
                            <span>${formattedDeadline}</span>
                        </div>
                        <span>${namaTugas}</span>`;
                    
                    if (tugasKosongMendatang) { tugasKosongMendatang.remove(); }
                    tugasMendatangList.appendChild(newTugasMendatang);

                    tugasForm.reset();
                    alert(`Tugas "${namaTugas}" berhasil ditambahkan!`);
                    resetToListView(); 

                } else {
                    alert("Harap lengkapi semua field tugas!");
                }
            });
        }
    }
});