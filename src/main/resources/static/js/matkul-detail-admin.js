document.addEventListener("DOMContentLoaded", () => {
    
    const mkTabDiv = document.querySelector('.mk-tab');
    const mataKuliahId = mkTabDiv ? mkTabDiv.getAttribute('data-mk-kode') : null;

    const tabs = document.querySelectorAll('.mk-tab button');

    // Logika Logout 
    
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
    
    // Pencarian dan Pagination 

    const searchBox = document.querySelector('.search-box');
    const searchInput = searchBox ? searchBox.querySelector('input[type="text"]') : null;
    const searchButton = searchBox ? searchBox.querySelector('button') : null;
    const pesertaCountSpan = document.querySelector('.peserta-count');
    const tableBody = document.querySelector('#peserta-table tbody');
    
    const allTableRows = tableBody ? Array.from(tableBody.querySelectorAll('tr')) : [];
   
    // Baris data tugas hanya memiliki 2 kolom (No dan Nama Tugas)
    const masterDataRows = allTableRows.filter(row => row.children.length === 2); 
    
    // Asumsi row kosong adalah row yang bukan data
    const noDataRow = tableBody ? allTableRows.find(row => row.children.length !== 2) : null; 
    
    let filteredPageItems = masterDataRows;
    
    const prevButton = document.getElementById('prev-page'); // Asumsi ada pagination
    const nextButton = document.getElementById('next-page'); // Asumsi ada pagination
    const pageInfoSpan = document.getElementById('current-page'); // Asumsi ada pagination

    const itemsPerPage = 3; 
    let totalPages = 0;
    let currentPage = 1;

    // Pagination

    const showPage = (page) => {
        const start = (page - 1) * itemsPerPage;
        const end = page * itemsPerPage;
    
        masterDataRows.forEach(item => {
            item.style.display = 'none';
        });

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
            pesertaCountSpan.textContent = `Total Tugas: ${filteredPageItems.length}`; 
        }
        
        // Gak ada datanya (Setelah filter)
        if (filteredPageItems.length === 0) {
            
            // Tampilkan baris pesan kosong jika ada
            if (noDataRow) {
                noDataRow.style.display = 'table-row'; 
            }
            if (pageInfoSpan) pageInfoSpan.textContent = `0 dari 0`;
            if (prevButton) prevButton.disabled = true;
            if (nextButton) nextButton.disabled = true;
            
            masterDataRows.forEach(row => row.style.display = 'none');
            
            return;
        }
        
        // Ada datanya 
        if (noDataRow) {
            noDataRow.style.display = 'none'; // Sembunyikan baris pesan kosong
        }

        if (pageInfoSpan) pageInfoSpan.textContent = `${currentPage} dari ${totalPages}`;
        
        // Handle pagination buttons (walaupun pagination mungkin tidak ada di HTML ini)
        if (prevButton) prevButton.disabled = currentPage === 1;
        if (nextButton) nextButton.disabled = currentPage === totalPages;

        showPage(currentPage);
    };

    // Search (Live Search)
    const handleSearch = () => {
        const query = searchInput.value.toLowerCase().trim();
        
        if (query === "") {
            filteredPageItems = masterDataRows; 
        } else {
            filteredPageItems = masterDataRows.filter(row => {
                if (row.cells.length < 2) return false;
                
                // Kolom Nama Tugas Besar ada di index 1 (setelah index 0: No)
                const nameCell = row.cells[1]; 
                
                // Filter hanya berdasarkan Nama Tugas
                const matchesName = nameCell && nameCell.textContent.toLowerCase().includes(query);

                return matchesName; 
            });
        }
        
        updateUI(true); // Reset halaman ke 1 setelah pencarian baru
    };
    
    if (searchInput) {
        searchInput.addEventListener('input', handleSearch); 
    }
    // Menghapus event listener lama
    if (searchButton) {
        searchButton.removeEventListener('click', handleSearch);
    }
    if (searchInput) {
        searchInput.removeEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                handleSearch();
            }
        });
    }

    // Event Listener pagination
    
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
    
    updateUI(); 
});