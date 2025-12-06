document.addEventListener("DOMContentLoaded", () => {
    // --- Logika Tab Navigation ---
    const currentPath = window.location.pathname;
    const basePath = currentPath.includes('/dosen/') ? '/dosen' : '/mahasiswa';

    const tabs = document.querySelectorAll('.mk-tab button');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const tabName = tab.textContent;

            if (tabName === 'Daftar Peserta') {
                window.location.href = basePath + '/matkul-peserta';
            } else if (tabName === 'Kuliah') {
                window.location.href = basePath + '/matkul-detail';
            }
        });
    });

    // ------------------------------------------------------------------
    // --- Logika Pencarian ---
    // ------------------------------------------------------------------

    const searchBox = document.querySelector('.search-box');
    const searchInput = searchBox.querySelector('input[type="text"]');
    const searchButton = searchBox.querySelector('button');
    const pesertaCountSpan = document.querySelector('.peserta-count');
    
    // ------------------------------------------------------------------
    // --- Logika Pagination (Elemen yang sama digunakan untuk search) ---
    // ------------------------------------------------------------------

    const tableBody = document.querySelector('#peserta-table tbody');
    // Ambil semua baris peserta DARI HTML (master list)
    const masterPageItems = tableBody ? Array.from(tableBody.querySelectorAll('.page-item')) : [];
    
    // Variabel yang akan menyimpan baris yang sedang ditampilkan/difilter
    let filteredPageItems = masterPageItems;
    
    // Elemen Pagination
    const prevButton = document.getElementById('prev-page');
    const nextButton = document.getElementById('next-page');
    const pageInfoSpan = document.getElementById('current-page');

    const itemsPerPage = 3; 
    let totalPages = Math.ceil(filteredPageItems.length / itemsPerPage);
    let currentPage = 1;

    // --- FUNGSI UTAMA PAGINASI (Diperbarui untuk bekerja dengan filteredPageItems) ---

    // Fungsi untuk menampilkan item pada halaman tertentu
    const showPage = (page) => {
        const start = (page - 1) * itemsPerPage;
        const end = page * itemsPerPage;
        
        // Sembunyikan semua baris master terlebih dahulu
        masterPageItems.forEach(item => {
            item.style.display = 'none';
        });

        // Tampilkan hanya baris yang ada di filteredPageItems untuk halaman saat ini
        filteredPageItems.forEach((item, index) => {
            if (index >= start && index < end) {
                item.style.display = 'table-row';
            }
        });
    };

    // Fungsi untuk mengupdate UI (tombol, info halaman, dan count)
    const updateUI = (resetPage = false) => {
        if (resetPage) {
            currentPage = 1;
        }
        
        totalPages = Math.ceil(filteredPageItems.length / itemsPerPage);
        
        if (pesertaCountSpan) {
            pesertaCountSpan.textContent = `Peserta: ${filteredPageItems.length} peserta`;
        }

        if (pageInfoSpan) pageInfoSpan.textContent = `${currentPage} dari ${totalPages || 1}`;
        
        if (prevButton) prevButton.disabled = currentPage === 1;
        if (nextButton) nextButton.disabled = currentPage === totalPages || totalPages === 0;

        showPage(currentPage);
    };

    // ------------------------------------------------------------------
    // --- FUNGSI PENCARIAN BARU ---
    // ------------------------------------------------------------------

    const handleSearch = () => {
        const query = searchInput.value.toLowerCase().trim();
        
        if (query === "") {
            // Jika query kosong, tampilkan semua item
            filteredPageItems = masterPageItems;
        } else {
            // Filter baris berdasarkan nama (Kolom index 1)
            filteredPageItems = masterPageItems.filter(row => {
                // Ambil teks dari kolom kedua (index 1)
                const nameCell = row.cells[1]; 
                return nameCell && nameCell.textContent.toLowerCase().includes(query);
            });
        }
        
        // Update tampilan UI dan reset ke halaman 1
        updateUI(true); 
    };

    // --- EVENT LISTENERS PENCARIAN ---
    
    // 1. Klik tombol Cari
    if (searchButton) {
        searchButton.addEventListener('click', handleSearch);
    }

    // 2. Tekan Enter pada input field
    if (searchInput) {
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                handleSearch();
            }
        });
    }


    // --- EVENT LISTENERS PAGINATION (Memanggil updateUI) ---
    
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

    // --- LOGIKA LOGOUT & INISIALISASI ---
    
    // Definisikan fungsi logout
    const handleLogout = () => {
        console.log("Melakukan proses logout..."); 
        window.location.href = '/'; 
    };

    // Pasang event listener ke tombol logout
    const logoutButton = document.getElementById('logoutButton') || document.querySelector('.logout'); 

    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    }
    
    // Inisialisasi tampilan awal (panggil setelah semua fungsi didefinisikan)
    updateUI(); 
});