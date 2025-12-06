document.addEventListener("DOMContentLoaded", () => {
    
    // --- Ambil KodeMK untuk Navigasi Tab ---
    const mkTabDiv = document.querySelector('.mk-tab');
    const mataKuliahId = mkTabDiv ? mkTabDiv.getAttribute('data-mk-kode') : null;

    // --- Logika Tab Navigation (DISESUAIKAN UNTUK DOSEN) ---
    const tabs = document.querySelectorAll('.mk-tab button');
    
    tabs.forEach(tab => {
        const tabTarget = tab.getAttribute('data-tab-target');

        // NAVIGASI KE TAB KULIAH (DETAIL DOSEN)
        if (tabTarget === 'kuliah' && mataKuliahId) {
            tab.addEventListener('click', () => {
                // Menggunakan endpoint /admin/arsip-matkul-detail dan parameter 'kodeMk'
                window.location.href = `/admin/arsip-matkul-detail?kodeMk=${encodeURIComponent(mataKuliahId)}`;
            });
        }
        // Tambahkan logic untuk tab 'Nilai' jika sudah ada endpoint-nya
    });

    // ------------------------------------------------------------------
    // --- Logika Logout (Untuk tombol di header) ---
    // ------------------------------------------------------------------
    
    const handleLogout = () => {
        fetch('/logout', { method: 'POST' }) 
            .then(() => {
                 window.location.href = '/'; 
            })
            .catch(() => {
                 window.location.href = '/'; 
            });
    };

    const logoutButton = document.getElementById('logoutButton') || document.querySelector('.logout'); 
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    }
    
    // ------------------------------------------------------------------
    // --- Logika Pencarian & Pagination ---
    // ------------------------------------------------------------------

    const searchBox = document.querySelector('.search-box');
    const searchInput = searchBox ? searchBox.querySelector('input[type="text"]') : null;
    const searchButton = searchBox ? searchBox.querySelector('button') : null;
    const pesertaCountSpan = document.querySelector('.peserta-count');
    const tableBody = document.querySelector('#peserta-table tbody');
    
    // Dapatkan SEMUA TR dari tbody
    const allTableRows = tableBody ? Array.from(tableBody.querySelectorAll('tr')) : [];
    
    // Baris DATA (yang memiliki 4 kolom, hasil render Thymeleaf)
    const masterDataRows = allTableRows.filter(row => row.children.length === 4); 
    
    // Baris pesan "Tidak ada peserta terdaftar..." (baris dengan colspan)
    const noDataRow = tableBody ? allTableRows.find(row => row.children.length !== 4) : null; 
    
    let filteredPageItems = masterDataRows;
    
    const prevButton = document.getElementById('prev-page');
    const nextButton = document.getElementById('next-page');
    const pageInfoSpan = document.getElementById('current-page');

    const itemsPerPage = 3; // Menggunakan 3 item per halaman (sesuai contoh Anda)
    let totalPages = 0;
    let currentPage = 1;

    // --- FUNGSI UTAMA PAGINASI ---

    const showPage = (page) => {
        const start = (page - 1) * itemsPerPage;
        const end = page * itemsPerPage;
        
        // Sembunyikan semua baris data awal (master list)
        masterDataRows.forEach(item => {
            item.style.display = 'none';
        });

        // Tampilkan hanya baris yang difilter untuk halaman saat ini
        filteredPageItems.forEach((item, index) => {
            if (index >= start && index < end) {
                item.style.display = 'table-row';
            }
        });
    };

    const updateUI = (resetPage = false) => {
        if (resetPage) {
            currentPage = 1;
        }
        
        totalPages = Math.ceil(filteredPageItems.length / itemsPerPage);
        
        if (pesertaCountSpan) {
            pesertaCountSpan.textContent = `Peserta: ${filteredPageItems.length} peserta`; 
        }
        
        // --- HANDLE KETIKA HASIL PENCARIAN 0 ---
        if (filteredPageItems.length === 0) {
            
            // Sembunyikan baris pesan default jika ada, karena tabel harus bersih
            if (noDataRow) {
                noDataRow.style.display = 'none'; 
            }
            if (pageInfoSpan) pageInfoSpan.textContent = `0 dari 0`;
            if (prevButton) prevButton.disabled = true;
            if (nextButton) nextButton.disabled = true;
            
            // Penting: Pastikan semua baris tersembunyi
            masterDataRows.forEach(row => row.style.display = 'none');
            
            return;
        }
        
        // --- HANDLE KETIKA HASIL PENCARIAN > 0 ---
        if (noDataRow) {
            noDataRow.style.display = 'none'; // Pastikan pesan default disembunyikan
        }

        if (pageInfoSpan) pageInfoSpan.textContent = `${currentPage} dari ${totalPages}`;
        
        if (prevButton) prevButton.disabled = currentPage === 1;
        if (nextButton) nextButton.disabled = currentPage === totalPages;

        showPage(currentPage);
    };

    // ------------------------------------------------------------------
    // --- FUNGSI PENCARIAN ---
    // ------------------------------------------------------------------

    const handleSearch = () => {
        const query = searchInput.value.toLowerCase().trim();
        
        if (query === "") {
            filteredPageItems = masterDataRows; // Kembalikan ke master list penuh
        } else {
            // Filter baris berdasarkan Nama (Cell index 1) atau NIM (Cell index 2)
            filteredPageItems = masterDataRows.filter(row => {
                if (row.cells.length < 3) return false;
                
                const nameCell = row.cells[1]; 
                const nimCell = row.cells[2];  
                
                const matchesName = nameCell && nameCell.textContent.toLowerCase().includes(query);
                const matchesNim = nimCell && nimCell.textContent.toLowerCase().includes(query);

                return matchesName || matchesNim;
            });
        }
        
        updateUI(true); 
    };

    // --- EVENT LISTENERS PENCARIAN ---
    
    if (searchButton) {
        searchButton.addEventListener('click', handleSearch);
    }

    if (searchInput) {
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                handleSearch();
            }
        });
    }

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
    
    // --- INISIALISASI ---
    updateUI(); 
});