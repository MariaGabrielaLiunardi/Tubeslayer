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
    
    // Pencarian & Pagination 

    const searchBox = document.querySelector('.search-box');
    const searchInput = searchBox ? searchBox.querySelector('input[type="text"]') : null;
    const searchButton = searchBox ? searchBox.querySelector('button') : null;
    const pesertaCountSpan = document.querySelector('.peserta-count');
    const tableBody = document.querySelector('#peserta-table tbody');
    
    const allTableRows = tableBody ? Array.from(tableBody.querySelectorAll('tr')) : [];
   
    const masterDataRows = allTableRows.filter(row => row.children.length === 2); 
    
    const noDataRow = tableBody ? allTableRows.find(row => row.children.length !== 2) : null; 
    
    let filteredPageItems = masterDataRows;
    
    const prevButton = document.getElementById('prev-page');
    const nextButton = document.getElementById('next-page');
    const pageInfoSpan = document.getElementById('current-page');

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
            pesertaCountSpan.textContent = `Tugas: ${filteredPageItems.length} tugas`; 
        }
        
        // Gak ada datanya
        if (filteredPageItems.length === 0) {
            
            if (noDataRow) {
                noDataRow.style.display = 'none'; 
            }
            if (pageInfoSpan) pageInfoSpan.textContent = `0 dari 0`;
            if (prevButton) prevButton.disabled = true;
            if (nextButton) nextButton.disabled = true;
            
            masterDataRows.forEach(row => row.style.display = 'none');
            
            return;
        }
        
        // Ada datanya 
        if (noDataRow) {
            noDataRow.style.display = 'none'; 
        }

        if (pageInfoSpan) pageInfoSpan.textContent = `${currentPage} dari ${totalPages}`;
        
        if (prevButton) prevButton.disabled = currentPage === 1;
        if (nextButton) nextButton.disabled = currentPage === totalPages;

        showPage(currentPage);
    };

    // Search 

    const handleSearch = () => {
        const query = searchInput.value.toLowerCase().trim();
        
        if (query === "") {
            filteredPageItems = masterDataRows; 
        } else {
            filteredPageItems = masterDataRows.filter(row => {
                if (row.cells.length < 2) return false;
                
                const nameCell = row.cells[1]; 
                
                const matchesName = nameCell && nameCell.textContent.toLowerCase().includes(query);

                return matchesName; 
            });
        }
        
        updateUI(true); 
    };
    
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

    // Event Listener Pagination
    
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