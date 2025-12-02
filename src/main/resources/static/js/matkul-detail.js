document.addEventListener("DOMContentLoaded", () => {
    // --- Logika Tab Navigation ---
    const tabs = document.querySelectorAll('.mk-tab button');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const tabName = tab.textContent;

            // Pindah ke halaman peserta jika tab diklik
            if (tabName === 'Daftar Peserta') {
                window.location.href = '/mahasiswa/matkul-peserta';
            } else if (tabName === 'Kuliah') {
                // Refresh/Tetap di halaman detail
                window.location.href ='/mahasiswa/matkul-detail';
            }
        });
    });

    // --- Logika Halaman Detail (Form Toggle & Submit Tugas) ---

    const listTugasView = document.getElementById('list-tugas-view');
    const buatTugasView = document.getElementById('buat-tugas-view');
    const toggleButton = document.getElementById('toggle-tambah-tugas');
    const breadcrumb = document.querySelector('.breadcrumb');
    const tugasForm = document.getElementById('tugas-form');
    
    const tugasBesarList = document.querySelector('.tugas-besar-list');
    const tugasMendatangList = document.querySelector('.tugas-mendatang-list');
    
    const tugasKosongMendatang = document.getElementById('tugas-mendatang-kosong');
    const tugasKosongBesar = document.getElementById('tugas-besar-kosong');
    
    let isListView = true;
    const originalBreadcrumb = breadcrumb.innerHTML;

    // Fungsi untuk kembali ke List View
    const resetToListView = () => {
        if (!isListView) {
            isListView = true;
            listTugasView.style.display = 'block';
            buatTugasView.style.display = 'none';
            
            // Kembalikan breadcrumb ke awal
            breadcrumb.innerHTML = originalBreadcrumb;
        }
    };

    // Fungsi untuk mengganti tampilan (Toggle)
    const toggleView = () => {
        isListView = !isListView;
        
        if (isListView) {
            resetToListView();
            
        } else {
            // Pindah ke tampilan Form Buat Tugas
            listTugasView.style.display = 'none';
            buatTugasView.style.display = 'block';
            
            // Ubah breadcrumb (HANYA DISPLAY, TIDAK ADA FUNGSI NAVIGASI)
            breadcrumb.innerHTML = originalBreadcrumb + ` > <b>Tambah Tugas</b>`;
        }
    };

    // Event listener untuk tombol Tambah Tugas di List View
    if (toggleButton) {
        toggleButton.addEventListener('click', toggleView);
    }
    
    // --- SUBMIT FORM TUGAS ---
    if (tugasForm) {
        tugasForm.addEventListener('submit', (event) => {
            event.preventDefault(); 
            
            const namaTugas = document.getElementById('nama-tugas').value;
            const deadline = document.getElementById('deadline').value;
            let deskripsi = document.getElementById('deskripsi-tugas').value;
            
            if (namaTugas && deadline && deskripsi) {
                
                // Format deadline
                const dateObj = new Date(deadline);
                const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
                const formattedDeadline = dateObj.toLocaleDateString('id-ID', options);
                
                const previewDeskripsi = deskripsi.substring(0, 150) + (deskripsi.length > 150 ? '...' : '');

                // 1. Tambahkan ke List Tugas Besar (Vertikal)
                const newTugasBesar = document.createElement('div');
                newTugasBesar.classList.add('tugas-besar');
                newTugasBesar.innerHTML = `
                    <div class="tugas-header">
                        <img src="/icon/blue-folder.png" class="folder-icon">
                        <h3>${namaTugas}</h3>
                    </div>
                    <p>
                        ${previewDeskripsi} 
                    </p>
                    <button class="btn-lihat">Lihat Tugas ></button>
                `;
                
                if (tugasBesarList) {
                    if (tugasKosongBesar) {
                        tugasKosongBesar.remove();
                    }
                    tugasBesarList.appendChild(newTugasBesar);
                }
                
                // 2. Tambahkan ke List Tugas Mendatang
                const newTugasMendatang = document.createElement('div');
                newTugasMendatang.classList.add('tugas-mendatang-card');
                newTugasMendatang.innerHTML = `
                    <div class="deadline-info">
                        <img src="/icon/kalender.png" class="kalender-icon" alt="kalender">
                        <span>${formattedDeadline}</span>
                    </div>
                    <span>${namaTugas}</span>
                `;
                
                if (tugasKosongMendatang) {
                    tugasKosongMendatang.remove();
                }
                
                tugasMendatangList.appendChild(newTugasMendatang);

                
                // 3. Reset form dan kembali ke list view
                tugasForm.reset();
                resetToListView(); 

            } else {
                alert("Harap lengkapi semua field tugas!");
            }
        });
    }
});