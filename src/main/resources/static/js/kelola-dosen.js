/* ===============================
       1. SIDEBAR TOGGLE
    ================================ */
const sidebar = document.querySelector('.sidebar');
const toggle = document.querySelector('.toggle');

if (sidebar && toggle) {
    toggle.addEventListener('click', () => {
        sidebar.classList.toggle('close');
    });
}

/* ===============================
       2. NAV ACTIVE HIGHLIGHT
    ================================ */
const navLinks = document.querySelectorAll('.sidebar .nav-link');

navLinks.forEach(li => {
    const anchor = li.querySelector("a");

    anchor.addEventListener("click", () => {
        navLinks.forEach(link => link.classList.remove("active"));
        li.classList.add("active");
    });
});

document.addEventListener("DOMContentLoaded", () => {
    console.log("Kelola Dosen JS Loaded");

    // ==================== CONFIGURATION ====================
    const API_BASE_URL = window.API_BASE_URL || window.location.origin;
    const API_DOSEN = window.API_DOSEN || `${API_BASE_URL}/admin/api/dosen`;
    
    console.log("Using API:", API_DOSEN);

    // ==================== ELEMENTS ====================
    const elements = {
        // Main container
        home: document.querySelector('.home'),
        
        // Main views
        tableView: document.getElementById("table-view"),
        footerView: document.getElementById("footer-view"),
        searchbar: document.getElementById("search-bar"),
        paginationContainer: document.querySelector(".pagination-container"),
        
        // Add dosen flow
        pilihCara: document.getElementById("pilih-cara"),
        importView: document.getElementById("import-dosen"),
        manualView: document.getElementById("tambah-dosen"),
        
        // Delete dosen flow
        hapusView: document.getElementById("view-hapus-dosen"),
        konfirmasiHapus: document.getElementById("konfirmasi-hapus"),
        
        // Buttons
        btnAdd: document.getElementById("btn-add"),
        btnDelete: document.getElementById("btn-delete"),
        btnImport: document.getElementById("btn-import"),
        btnManual: document.getElementById("btn-manual"),
        
        // Forms
        tambahForm: document.getElementById("tambah-dosen-form"),
        
        // Titles
        listTitle: document.getElementById("list-title"),
        subTitle: document.getElementById("sub-title"),
        subTitle2: document.getElementById("sub-title-2"),
        
        // Search elements
        suggestionsBox: document.getElementById("suggestions"),
        searchInput: document.getElementById("search-input"),
        
        // Other buttons
        btnCancelDelete: document.getElementById("btn-cancel-delete"),
        btnConfirmDelete: document.getElementById("btn-confirm-delete"),
        btnCancelConfirm: document.getElementById("btn-cancel-confirm"),
        btnConfirmDeleteFinal: document.getElementById("btn-confirm-delete-final"),
        btnPilihFile: document.getElementById("btn-pilih-file"),
        fileInput: document.getElementById("file-input"),

        // Pagination elements
        prevPageBtn: document.getElementById("prev-page"),
        nextPageBtn: document.getElementById("next-page"),
        pageInfo: document.getElementById("current-page")
    };

    // Log elements for debugging
    console.log("Elements found:");
    Object.keys(elements).forEach(key => {
        console.log(`- ${key}:`, elements[key] ? "✓" : "✗");
    });

    // ==================== STATE ====================
    let daftarDosen = [];
    let selectedDosen = null;
    let filteredData = [];
    let currentPage = 1;
    const itemsPerPage = 3; // 3 data per halaman

    // ==================== INITIALIZATION ====================
    init();

    async function init() {
        // First: Hide ALL views except main
        hideAllViews();
        showMainView();
        
        // Setup event listeners
        setupEventListeners();
        
        // Load initial data
        await loadDosenData();
        
        // Setup search
        setupSearch();
    }

    // ==================== VIEW MANAGEMENT ====================
    function hideAllViews() {
        // Hide all special views
        const viewsToHide = [
            elements.pilihCara,
            elements.importView,
            elements.manualView,
            elements.hapusView,
            elements.konfirmasiHapus
        ];
        
        viewsToHide.forEach(view => {
            if (view) view.style.display = 'none';
        });
        
        // Also hide main table elements initially
        if (elements.tableView) elements.tableView.style.display = 'none';
        if (elements.footerView) elements.footerView.style.display = 'none';
        if (elements.searchbar) elements.searchbar.style.display = 'none';
        if (elements.paginationContainer) elements.paginationContainer.style.display = 'none';
        
        // Clear titles
        if (elements.subTitle) elements.subTitle.textContent = '';
        if (elements.subTitle2) elements.subTitle2.textContent = '';
    }

    function showMainView() {
        console.log("Showing main view");
        
        // Hide all special views
        hideAllViews();
        
        // Show main table elements
        if (elements.tableView) {
            elements.tableView.style.display = 'block';
        }
        if (elements.footerView) {
            elements.footerView.style.display = 'flex';
        }
        if (elements.searchbar) {
            elements.searchbar.style.display = 'block';
        }
        if (elements.paginationContainer) {
            elements.paginationContainer.style.display = 'flex';
        }
        
        // Reset titles
        if (elements.subTitle) elements.subTitle.textContent = '';
        if (elements.subTitle2) elements.subTitle2.textContent = '';
        
        // Clear selected dosen
        selectedDosen = null;
        
        // Reset search input if exists
        if (elements.searchInput) elements.searchInput.value = '';
        if (elements.suggestionsBox) {
            elements.suggestionsBox.innerHTML = '';
            elements.suggestionsBox.style.display = 'none';
        }
    }

    function showPilihCaraView() {
        console.log("Showing pilih cara view");
        hideAllViews();
        if (elements.pilihCara) {
            elements.pilihCara.style.display = 'flex';
        }
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Dosen";
        if (elements.subTitle2) elements.subTitle2.textContent = "";
    }

    function showImportView() {
        console.log("Showing import view");
        hideAllViews();
        if (elements.importView) {
            elements.importView.style.display = 'flex';
        }
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Dosen";
        if (elements.subTitle2) elements.subTitle2.textContent = " > Import";
    }

    function showManualView() {
        console.log("Showing manual view");
        hideAllViews();
        if (elements.manualView) {
            elements.manualView.style.display = 'flex';
        }
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Dosen";
        if (elements.subTitle2) elements.subTitle2.textContent = " > Tambah Baru";
    }

    function showHapusView() {
        console.log("Showing hapus view");
        hideAllViews();
        if (elements.hapusView) {
            elements.hapusView.style.display = 'flex';
        }
        if (elements.subTitle) elements.subTitle.textContent = " > Hapus Dosen";
        if (elements.subTitle2) elements.subTitle2.textContent = "";
        
        // Setup search for delete
        setupDeleteSearch();
    }

    function showKonfirmasiHapusView() {
        console.log("Showing konfirmasi hapus view");
        hideAllViews();
        if (elements.konfirmasiHapus) {
            elements.konfirmasiHapus.style.display = 'flex';
        }
    }

    // ==================== API FUNCTIONS ====================
    async function loadDosenData() {
        try {
            showLoading(true);
            console.log("Loading data from:", API_DOSEN);
            
            const response = await fetch(API_DOSEN);
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            const result = await response.json();
            console.log("API Response:", result);
            
            if (result.success) {
                daftarDosen = result.data || [];
                filteredData = []; // Reset filter
                currentPage = 1;   // Reset to page 1
                renderDosenTable();
                updateCount(daftarDosen.length);
                console.log(`Loaded ${daftarDosen.length} dosen`);
            } else {
                throw new Error(result.message);
            }
        } catch (error) {
            console.error("Error loading data:", error);
            showMessage("Gagal memuat data: " + error.message, "error");
            
            // Fallback: extract from existing HTML
            extractDataFromExistingTable();
            filteredData = [];
            currentPage = 1;
            renderDosenTable();
        } finally {
            showLoading(false);
        }
    }

    async function addDosen(dosenData) {
        try {
            const response = await fetch(API_DOSEN, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(dosenData)
            });
            
            const result = await response.json();
            
            if (!response.ok) {
                throw new Error(result.message || `HTTP ${response.status}`);
            }
            
            return result;
        } catch (error) {
            console.error("Error adding dosen:", error);
            throw error;
        }
    }

    async function deleteDosen(id) {
        try {
            const response = await fetch(`${API_DOSEN}/${id}`, {
                method: 'DELETE',
                headers: {
                    'Accept': 'application/json'
                }
            });
            
            const result = await response.json();
            
            if (!response.ok) {
                throw new Error(result.message || `HTTP ${response.status}`);
            }
            
            return result;
        } catch (error) {
            console.error("Error deleting dosen:", error);
            throw error;
        }
    }

    async function importDosenFile(file) {
        try {
            const formData = new FormData();
            formData.append('file', file);
            
            const response = await fetch(`${API_DOSEN}/import`, {
                method: 'POST',
                body: formData
            });
            
            const result = await response.json();
            
            if (!response.ok) {
                throw new Error(result.message || `HTTP ${response.status}`);
            }
            
            return result;
        } catch (error) {
            console.error("Error importing file:", error);
            throw error;
        }
    }

    // ==================== RENDER FUNCTIONS ====================
    function getActiveData() {
        return filteredData.length > 0 ? filteredData : daftarDosen;
    }

    function updatePaginationInfo(totalPages) {
        if (elements.pageInfo) {
            elements.pageInfo.textContent = `${totalPages === 0 ? 0 : currentPage} / ${Math.max(totalPages, 1)}`;
        }
        
        // Update button states
        if (elements.prevPageBtn) {
            elements.prevPageBtn.disabled = currentPage === 1;
        }
        
        if (elements.nextPageBtn) {
            elements.nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;
        }
    }

function renderDosenTable() {
    if (!elements.tableView) {
        console.error("Table view element not found!");
        return;
    }
    
    // Clear existing data rows
    const existingRows = elements.tableView.querySelectorAll('.data-row');
    existingRows.forEach(row => {
        if (row.parentNode) {
            row.parentNode.removeChild(row);
        }
    });
    
    const dataToShow = getActiveData();
    const totalItems = dataToShow.length;
    const totalPages = Math.max(1, Math.ceil(totalItems / itemsPerPage));
    
    console.log(`📊 Total items: ${totalItems}, Items per page: ${itemsPerPage}, Total pages: ${totalPages}`);
    
    // Adjust current page if out of bounds
    if (currentPage > totalPages) currentPage = Math.max(1, totalPages);
    if (currentPage < 1) currentPage = 1;
    
    const start = (currentPage - 1) * itemsPerPage;
    const end = Math.min(start + itemsPerPage, totalItems);
    const pageItems = dataToShow.slice(start, end);
    
    console.log(`🔄 Rendering page ${currentPage} of ${totalPages}, items ${start + 1}-${end} of ${totalItems}`);
    
    // Add rows for current page
    pageItems.forEach((dosen, index) => {
        const row = document.createElement('div');
        row.className = 'data-row';
        
        const statusText = dosen.status || (dosen.isActive ? 'Aktif' : 'Nonaktif');
        const statusClass = statusText === 'Aktif' ? 'active' : 'inactive';
        
        row.innerHTML = `
            <span>${start + index + 1}.</span>
            <span>${dosen.nip || dosen.id || '-'}</span>
            <span>${dosen.nama || '-'}</span>
            <span>${dosen.email || '-'}</span>
            <span>
                <span class="status-badge ${statusClass}">
                    ${statusText}
                </span>
            </span>
        `;
        
        elements.tableView.appendChild(row);
    });
    
    // Show empty message if no data
    if (pageItems.length === 0) {
        const emptyRow = document.createElement('div');
        emptyRow.className = 'data-row';
        emptyRow.style.textAlign = 'center';
        emptyRow.style.padding = '20px';
        emptyRow.style.color = '#666';
        emptyRow.innerHTML = `
            <span style="display: block; width: 100%;">
                Tidak ada data dosen
            </span>
        `;
        elements.tableView.appendChild(emptyRow);
    }
    
    updatePaginationInfo(totalPages);
    updateCount(totalItems);
    console.log(`✅ Page ${currentPage}/${totalPages} rendered with ${pageItems.length} items`);
}

    function extractDataFromExistingTable() {
        if (!elements.tableView) return;
        
        const rows = elements.tableView.querySelectorAll('.data-row');
        daftarDosen = [];
        
        rows.forEach((row, index) => {
            const cells = row.querySelectorAll('span');
            if (cells.length >= 5) {
                const nama = cells[2].textContent.trim();
                if (nama && nama !== 'Nama Dosen' && nama !== '-') {
                    daftarDosen.push({
                        id: cells[1].textContent.trim() || `DSN${index + 1}`,
                        nip: cells[1].textContent.trim(),
                        nama: nama,
                        email: cells[3].textContent.trim() !== '-' ? cells[3].textContent.trim() : null,
                        status: cells[4].textContent.trim(),
                        isActive: cells[4].textContent.trim() === 'Aktif'
                    });
                }
            }
        });
        
        console.log(`Extracted ${daftarDosen.length} dosen from existing table`);
    }

    function updateCount(count) {
        if (!elements.listTitle) return;
        
        // Remove existing count span
        let countElement = elements.listTitle.querySelector('#dosen-count');
        if (countElement) {
            countElement.textContent = `(${count} data)`;
        } else {
            // Create new count span
            countElement = document.createElement('span');
            countElement.id = 'dosen-count';
            countElement.style.cssText = 'font-size: 0.8em; color: #666; margin-left: 8px; font-weight: normal;';
            countElement.textContent = `(${count} data)`;
            elements.listTitle.appendChild(countElement);
        }
    }

    // ==================== EVENT HANDLERS SETUP ====================
    function setupEventListeners() {
        console.log("Setting up event listeners");
        
        // Main navigation buttons
        if (elements.btnAdd) {
            elements.btnAdd.addEventListener('click', showPilihCaraView);
        }
        
        if (elements.btnDelete) {
            elements.btnDelete.addEventListener('click', showHapusView);
        }
        
        // Add dosen flow
        if (elements.btnImport) {
            elements.btnImport.addEventListener('click', showImportView);
        }
        
        if (elements.btnManual) {
            elements.btnManual.addEventListener('click', showManualView);
        }
        
        // Form submission
        if (elements.tambahForm) {
            elements.tambahForm.addEventListener('submit', handleFormSubmit);
        }
        
        // File upload
        if (elements.btnPilihFile) {
            elements.btnPilihFile.addEventListener('click', () => {
                if (elements.fileInput) elements.fileInput.click();
            });
        }
        
        if (elements.fileInput) {
            elements.fileInput.addEventListener('change', handleFileUpload);
        }
        
        // Delete flow
        if (elements.btnCancelDelete) {
            elements.btnCancelDelete.addEventListener('click', showMainView);
        }
        
        if (elements.btnConfirmDelete) {
            elements.btnConfirmDelete.addEventListener('click', handleConfirmDelete);
        }
        
        if (elements.btnCancelConfirm) {
            elements.btnCancelConfirm.addEventListener('click', showMainView);
        }
        
        if (elements.btnConfirmDeleteFinal) {
            elements.btnConfirmDeleteFinal.addEventListener('click', handleFinalDelete);
        }
        
        // Back to main when clicking title
        if (elements.listTitle) {
            elements.listTitle.addEventListener('click', showMainView);
        }
        
        // Search input for delete
        if (elements.searchInput) {
            elements.searchInput.addEventListener('input', handleDeleteSearch);
        }

        // Pagination buttons
        if (elements.prevPageBtn) {
            elements.prevPageBtn.addEventListener('click', () => {
                if (currentPage > 1) {
                    currentPage -= 1;
                    renderDosenTable();
                }
            });
        }
        
        if (elements.nextPageBtn) {
            elements.nextPageBtn.addEventListener('click', () => {
                const totalPages = Math.ceil(getActiveData().length / itemsPerPage) || 1;
                if (currentPage < totalPages) {
                    currentPage += 1;
                    renderDosenTable();
                }
            });
        }
        
        // Close suggestions when clicking outside
        document.addEventListener('click', (e) => {
            if (elements.suggestionsBox && !elements.suggestionsBox.contains(e.target) && 
                elements.searchInput && !elements.searchInput.contains(e.target)) {
                elements.suggestionsBox.style.display = 'none';
            }
        });
        
        console.log("Event listeners setup complete");
    }

    // ==================== EVENT HANDLERS ====================
    async function handleFormSubmit(e) {
        e.preventDefault();
        
        console.log("🔄 Form tambah dosen submitted");
        
        const nip = document.getElementById("nip-dosen")?.value.trim();
        const nama = document.getElementById("nama-dosen")?.value.trim();
        const email = document.getElementById("email-dosen")?.value.trim();
        
        console.log("📝 Input values:", { nip, nama, email });
        
        // Validasi
        if (!nama) {
            console.error("❌ Nama kosong");
            showMessage("Nama dosen harus diisi", "error");
            return;
        }
        
        try {
            showLoading(true);
            
            // Format data sesuai controller
            const dosenData = {
                nama: nama
            };
            
            // Tambahkan NIP jika ada
            if (nip && nip.trim() !== "") {
                dosenData.nip = nip;
            }
            
            // Tambahkan email jika ada
            if (email && email.trim() !== "") {
                dosenData.email = email;
            }
            
            // TANPA status - otomatis aktif
            console.log("📤 Sending data to API:", dosenData);
            
            const result = await addDosen(dosenData);
            
            console.log("📥 API Response:", result);
            
            if (result.success) {
                showMessage("Dosen berhasil ditambahkan", "success");
                
                // Reset form
                if (elements.tambahForm) elements.tambahForm.reset();
                
                // Reload data
                await loadDosenData();
                
                // Return to main view
                showMainView();
            } else {
                throw new Error(result.message || "Gagal menambahkan dosen");
            }
        } catch (error) {
            console.error("❌ Form submission error:", error);
            showMessage("Gagal menambahkan dosen: " + error.message, "error");
        } finally {
            showLoading(false);
        }
    }

    function handleDeleteSearch(e) {
        const keyword = e.target.value.toLowerCase();
        if (!elements.suggestionsBox) return;
        
        elements.suggestionsBox.innerHTML = '';
        elements.suggestionsBox.style.display = 'none';
        
        if (!keyword) return;
        
        const filtered = daftarDosen.filter(dosen => 
            (dosen.nama || '').toLowerCase().includes(keyword) || 
            (dosen.nip || '').toLowerCase().includes(keyword) ||
            (dosen.email || '').toLowerCase().includes(keyword)
        );
        
        if (filtered.length === 0) return;
        
        elements.suggestionsBox.style.display = 'block';
        
        filtered.forEach(dosen => {
            const li = document.createElement('li');
            const kode = dosen.nip || dosen.id;
            const nama = dosen.nama;
            li.textContent = `${kode} - ${nama}`;
            li.dataset.id = dosen.id;
            li.dataset.nama = nama;
            li.dataset.kode = kode;
            
            li.addEventListener('click', () => {
                if (elements.searchInput) {
                    elements.searchInput.value = `${kode} - ${nama}`;
                }
                selectedDosen = {
                    id: dosen.id,
                    kode: kode,
                    nama: nama
                };
                elements.suggestionsBox.style.display = 'none';
            });
            
            elements.suggestionsBox.appendChild(li);
        });
    }

    function setupDeleteSearch() {
        if (!elements.suggestionsBox) return;
        
        elements.suggestionsBox.innerHTML = '';
        elements.suggestionsBox.style.display = 'none';
        
        daftarDosen.forEach(dosen => {
            const li = document.createElement('li');
            const kode = dosen.nip || dosen.id;
            const nama = dosen.nama;
            li.textContent = `${kode} - ${nama}`;
            li.dataset.id = dosen.id;
            li.dataset.nama = nama;
            li.dataset.kode = kode;
            
            li.addEventListener('click', () => {
                if (elements.searchInput) {
                    elements.searchInput.value = `${kode} - ${nama}`;
                }
                selectedDosen = {
                    id: dosen.id,
                    kode: kode,
                    nama: nama
                };
                elements.suggestionsBox.style.display = 'none';
            });
            
            elements.suggestionsBox.appendChild(li);
        });
    }

    function handleConfirmDelete() {
        if (!selectedDosen) {
            showMessage("Pilih dosen terlebih dahulu", "error");
            return;
        }
        
        // Update confirmation text
        const confirmText = document.getElementById("konfirmasi-hapus-text");
        if (confirmText) {
            confirmText.innerHTML = `
                Apakah Anda yakin ingin menghapus dosen:<br>
                <strong>${selectedDosen.kode} - ${selectedDosen.nama}</strong>
            `;
        }
        
        showKonfirmasiHapusView();
    }

    async function handleFinalDelete() {
        if (!selectedDosen) {
            showMessage("Tidak ada dosen yang dipilih", "error");
            return;
        }
        
        try {
            showLoading(true);
            
            const result = await deleteDosen(selectedDosen.id);
            
            if (result.success) {
                showMessage("Dosen berhasil dihapus", "success");
                
                // Reload data
                await loadDosenData();
                
                // Return to main view
                showMainView();
            }
        } catch (error) {
            console.error("Delete error:", error);
            showMessage("Gagal menghapus dosen: " + error.message, "error");
        } finally {
            showLoading(false);
        }
    }

    async function handleFileUpload(e) {
        const file = e.target.files[0];
        if (!file) return;
        
        // Validate file type
        const validTypes = ['text/csv', 'application/vnd.ms-excel', 
                           'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'];
        
        if (!validTypes.includes(file.type) && !file.name.match(/\.(csv|xlsx|xls)$/)) {
            showMessage("File harus berupa CSV atau Excel", "error");
            e.target.value = '';
            return;
        }
        
        try {
            showLoading(true);
            
            const result = await importDosenFile(file);
            
            if (result.success) {
                showMessage("File berhasil diimport", "success");
                
                // Reload data
                await loadDosenData();
                
                // Return to main view
                showMainView();
            } else {
                throw new Error(result.message || "Import gagal");
            }
        } catch (error) {
            console.error("File upload error:", error);
            showMessage("Gagal mengimpor file: " + error.message, "error");
        } finally {
            showLoading(false);
            e.target.value = '';
        }
    }

    function setupSearch() {
        const searchInput = elements.searchbar?.querySelector('input');
        if (!searchInput) return;
        
        searchInput.addEventListener('input', (e) => {
            const keyword = e.target.value.toLowerCase().trim();
            
            if (!keyword) {
                filteredData = [];
                currentPage = 1;
                renderDosenTable();
                return;
            }
            
            filteredData = daftarDosen.filter(dosen => 
                (dosen.nama || '').toLowerCase().includes(keyword) || 
                (dosen.nip || '').toLowerCase().includes(keyword) ||
                (dosen.email || '').toLowerCase().includes(keyword)
            );
            
            currentPage = 1;
            renderDosenTable();
        });
    }

    // ==================== UTILITY FUNCTIONS ====================
    function showLoading(show) {
        const loadingEl = document.getElementById('global-loading');
        if (loadingEl) {
            loadingEl.style.display = show ? 'flex' : 'none';
        }
    }

    function showMessage(message, type = 'info') {
        // Remove existing notifications
        const existing = document.querySelectorAll('.notification');
        existing.forEach(el => el.remove());
        
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        
        const bgColor = type === 'success' ? '#4CAF50' : 
                       type === 'error' ? '#f44336' : '#2196F3';
        const icon = type === 'success' ? '✓' : 
                    type === 'error' ? '✗' : 'ℹ';
        
        notification.innerHTML = `
            <div style="position: fixed; top: 20px; right: 20px; padding: 12px 20px; 
                        border-radius: 4px; color: white; z-index: 10000; font-size: 14px;
                        background: ${bgColor};
                        box-shadow: 0 2px 5px rgba(0,0,0,0.2); display: flex; align-items: center; gap: 10px;">
                ${icon} ${message}
            </div>
        `;
        
        document.body.appendChild(notification);
        
        // Auto remove after 3 seconds
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 3000);
    }

    // Add CSS for notifications if not already added
    if (!document.querySelector('#notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            .notification {
                animation: slideIn 0.3s ease;
            }
            @keyframes slideIn {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
        `;
        document.head.appendChild(style);
    }
});