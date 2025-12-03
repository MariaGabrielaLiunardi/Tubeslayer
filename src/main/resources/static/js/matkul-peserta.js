document.addEventListener("DOMContentLoaded", () => {
    // --- Logika Tab Navigation ---
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

    // --- Logika Pagination ---

    const tableBody = document.querySelector('#peserta-table tbody');
    // Ambil semua baris peserta dengan class 'page-item'
    const pageItems = tableBody ? Array.from(tableBody.querySelectorAll('.page-item')) : [];
    
    // Elemen Pagination
    const prevButton = document.getElementById('prev-page');
    const nextButton = document.getElementById('next-page');
    const pageInfoSpan = document.getElementById('current-page');

    const itemsPerPage = 3; 
    const totalPages = Math.ceil(pageItems.length / itemsPerPage);
    let currentPage = 1;

    // --- FUNGSI UTAMA PAGINASI ---

    // Fungsi untuk menampilkan item pada halaman tertentu
    const showPage = (page) => {
        const start = (page - 1) * itemsPerPage;
        const end = page * itemsPerPage;

        pageItems.forEach((item, index) => {
            item.style.display = 'none';

            if (index >= start && index < end) {
                item.style.display = 'table-row';
            }
        });
    };

    // Fungsi untuk mengupdate UI (tombol dan info halaman)
    const updateUI = () => {
        pageInfoSpan.textContent = `${currentPage} dari ${totalPages}`;
        
        prevButton.disabled = currentPage === 1;
        nextButton.disabled = currentPage === totalPages || totalPages === 0;

        showPage(currentPage);
    };

    // --- EVENT LISTENERS PAGINATION ---
    
    if (prevButton) {
        prevButton.addEventListener('click', () => {
            if (currentPage > 1) {
                currentPage--;
                updateUI();
            }
        });
    }

    if (nextButton) {
        nextButton.addEventListener('click', () => {
            if (currentPage < totalPages) {
                currentPage++;
                updateUI();
            }
        });
    }

    // Inisialisasi tampilan awal
    if (totalPages > 0) {
        updateUI();
    }
});