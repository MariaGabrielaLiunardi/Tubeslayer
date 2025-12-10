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
    console.log("Kelola Mahasiswa JS Loaded");

    // ==================== CONFIGURATION ====================
    const API_BASE_URL = window.API_BASE_URL || "http://localhost:8080"; // SAMAKAN DENGAN DOSEN
    const API_MAHASISWA = window.API_MAHASISWA || `${API_BASE_URL}/api/mahasiswa`; // SAMAKAN POLA
    console.log("API Mahasiswa:", API_MAHASISWA);

    // ==================== ELEMENTS ====================
    const elements = {
        // Main views
        tableView: document.getElementById("table-view"),
        footerView: document.getElementById("footer-view"),
        searchbar: document.getElementById("search-bar"),
        
        // Add mahasiswa flow
        pilihCara: document.getElementById("pilih-cara"),
        importView: document.getElementById("import-mahasiswa"),
        manualView: document.getElementById("tambah-mahasiswa"),
        
        // Delete mahasiswa flow
        hapusView: document.getElementById("view-hapus-mahasiswa"),
        konfirmasiHapus: document.getElementById("konfirmasi-hapus"),
        
        // Buttons
        btnAdd: document.getElementById("btn-add"),
        btnDelete: document.getElementById("btn-delete"),
        btnImport: document.getElementById("btn-import"),
        btnManual: document.getElementById("btn-manual"),
        
        // Forms
        tambahForm: document.getElementById("tambah-mahasiswa-form"),
        
        // Titles
        listTitle: document.getElementById("list-title"),
        subTitle: document.getElementById("sub-title"),
        subTitle2: document.getElementById("sub-title-2"),
        
        // Search elements
        suggestionsBox: document.getElementById("suggestions"),
        searchInput: document.getElementById("search-input"),
        buttonPage: document.getElementById("pagination"),
        
        // Other buttons
        btnCancelDelete: document.getElementById("btn-cancel-delete"),
        btnConfirmDelete: document.getElementById("btn-confirm-delete"),
        btnCancelConfirm: document.getElementById("btn-cancel-confirm"),
        btnConfirmDeleteFinal: document.getElementById("btn-confirm-delete-final"),
        btnPilihFile: document.getElementById("btn-pilih-file"),
        fileInput: document.getElementById("file-input")
    };

    // ==================== STATE ====================
    let daftarMahasiswa = [];
    let selectedMahasiswa = null;

    // ==================== INITIALIZATION ====================
    init();

    async function init() {

        showMainView();
        // Setup event listeners
        setupEventListeners();
        
        // Load initial data
        await loadMahasiswaData();
        
        // Setup search
        setupSearch();
    }

    // ==================== VIEW MANAGEMENT ====================
    function hideAllViews() {
        const views = [
            elements.pilihCara,
            elements.importView,
            elements.manualView,
            elements.hapusView,
            elements.konfirmasiHapus
        ];
        
        views.forEach(view => {
            if (view) view.style.display = 'none';
        });
    }

    function showMainView() {
        hideAllViews();
        
        // Show main table elements
        if (elements.tableView) elements.tableView.style.display = 'block';
        if (elements.footerView) elements.footerView.style.display = 'flex';
        if (elements.searchbar) elements.searchbar.style.display = 'block';
        elements.buttonPage.style.display = 'flex';
        
        // Clear titles
        if (elements.subTitle) elements.subTitle.textContent = '';
        if (elements.subTitle2) elements.subTitle2.textContent = '';
        
        // Clear selected
        selectedMahasiswa = null;
        
        // Reset search input
        if (elements.searchInput) {
            elements.searchInput.value = '';
            elements.searchInput.placeholder = 'Cari mahasiswa...';
        }
        if (elements.suggestionsBox) {
            elements.suggestionsBox.innerHTML = '';
            elements.suggestionsBox.style.display = 'none';
        }
    }

    function showPilihCaraView() {
        hideAllViews();
        if (elements.pilihCara) elements.pilihCara.style.display = 'flex';
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Mahasiswa";
        if (elements.subTitle2) elements.subTitle2.textContent = "";

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
        elements.buttonPage.style.display = 'none';

    }

    function showImportView() {
        hideAllViews();
        if (elements.importView) elements.importView.style.display = 'flex';
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Mahasiswa";
        if (elements.subTitle2) elements.subTitle2.textContent = " > Import Data";

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
        elements.buttonPage.style.display = 'none';
    }

    function showManualView() {
        hideAllViews();
        if (elements.manualView) elements.manualView.style.display = 'flex';
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Mahasiswa";
        if (elements.subTitle2) elements.subTitle2.textContent = " > Tambah Baru";

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
        elements.buttonPage.style.display = 'none';
    }

    function showHapusView() {
        hideAllViews();
        if (elements.hapusView) elements.hapusView.style.display = 'flex';
        if (elements.subTitle) elements.subTitle.textContent = " > Hapus Mahasiswa";
        if (elements.subTitle2) elements.subTitle2.textContent = "";
        
        setupDeleteSearch();
        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
        elements.buttonPage.style.display = 'none';
    }

    function showKonfirmasiHapusView() {
        hideAllViews();
        if (elements.konfirmasiHapus) elements.konfirmasiHapus.style.display = 'flex';

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
        elements.buttonPage.style.display = 'none';
    }

    // ==================== API FUNCTIONS ====================
    async function loadMahasiswaData() {
        try {
            showLoading(true);
            console.log("📡 Fetching from:", API_MAHASISWA);
            
            const response = await fetch(API_MAHASISWA, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            });
            
            console.log("Response Status:", response.status);
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            const result = await response.json();
            console.log("API Response:", result);
            
            if (result.status === "success" || result.success) {
                daftarMahasiswa = result.data || [];
                renderMahasiswaTable();
                updateCount(daftarMahasiswa.length);
                console.log(`✅ Loaded ${daftarMahasiswa.length} mahasiswa`);
            } else {
                throw new Error(result.message || "Failed to load data");
            }
        } catch (error) {
            console.error("❌ Error loading data:", error);
            showMessage("Gagal memuat data mahasiswa: " + error.message, "error");
            
            // Fallback: show empty state
            daftarMahasiswa = [];
            renderMahasiswaTable();
            updateCount(0);
        } finally {
            showLoading(false);
        }
    }

    async function addMahasiswa(mahasiswaData) {
        try {
            const response = await fetch(API_MAHASISWA, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(mahasiswaData)
            });
            
            const result = await response.json();
            
            if (!response.ok) {
                throw new Error(result.message || `HTTP ${response.status}`);
            }
            
            return result;
        } catch (error) {
            console.error("Error adding mahasiswa:", error);
            throw error;
        }
    }

    async function deleteMahasiswa(id) {
        try {
            const response = await fetch(`${API_MAHASISWA}/${id}`, {
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
            console.error("Error deleting mahasiswa:", error);
            throw error;
        }
    }

    async function importMahasiswaFile(file) {
        try {
            const formData = new FormData();
            formData.append('file', file);
            
            const response = await fetch(`${API_MAHASISWA}/import`, {
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
    function renderMahasiswaTable() {
        if (!elements.tableView) {
            console.error("Table view element not found!");
            return;
        }
        
        // Clear existing data rows (keep header)
        const existingRows = elements.tableView.querySelectorAll('.data-row');
        existingRows.forEach(row => {
            if (row.parentNode) {
                row.parentNode.removeChild(row);
            }
        });
        
        console.log("Rendering", daftarMahasiswa.length, "mahasiswa");
        
        // Add new rows
        if (daftarMahasiswa.length === 0) {
            const emptyRow = document.createElement('div');
            emptyRow.className = 'data-row';
            emptyRow.style.textAlign = 'center';
            emptyRow.style.padding = '20px';
            emptyRow.style.color = '#666';
            emptyRow.innerHTML = `
                <span style="grid-column: 1 / -1">Tidak ada data mahasiswa</span>
            `;
            elements.tableView.appendChild(emptyRow);
            return;
        }
        
        daftarMahasiswa.forEach((mahasiswa, index) => {
            const row = document.createElement('div');
            row.className = 'data-row';
            
            const statusText = mahasiswa.status || (mahasiswa.isActive ? 'Aktif' : 'Nonaktif');
            const statusClass = statusText === 'Aktif' ? 'active' : 'inactive';
            
            row.innerHTML = `
                <span>${index + 1}</span>
                <span>${mahasiswa.npm || mahasiswa.id || '-'}</span>
                <span>${mahasiswa.nama || '-'}</span>
                <span>${mahasiswa.email || '-'}</span>
                <span>
                    <span class="status-badge ${statusClass}">
                        ${statusText}
                    </span>
                </span>
            `;
            
            elements.tableView.appendChild(row);
        });
    }

    function updateCount(count) {
        if (!elements.listTitle) return;
        
        // Remove existing count span
        let countElement = elements.listTitle.querySelector('#mahasiswa-count');
        if (countElement) {
            countElement.textContent = `(${count} data)`;
        } else {
            // Create new count span
            countElement = document.createElement('span');
            countElement.id = 'mahasiswa-count';
            countElement.style.cssText = 'font-size: 0.8em; color: #666; margin-left: 8px; font-weight: normal;';
            countElement.textContent = `(${count} data)`;
            elements.listTitle.appendChild(countElement);
        }
    }

    // ==================== EVENT HANDLERS ====================
    function setupEventListeners() {
        console.log("Setting up event listeners");
        
        // Main navigation buttons
        if (elements.btnAdd) {
            elements.btnAdd.addEventListener('click', showPilihCaraView);
        }
        
        if (elements.btnDelete) {
            elements.btnDelete.addEventListener('click', showHapusView);
        }
        
        // Add mahasiswa flow
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
        
        // Close suggestions when clicking outside
        document.addEventListener('click', (e) => {
            if (elements.suggestionsBox && !elements.suggestionsBox.contains(e.target) && 
                elements.searchInput && !elements.searchInput.contains(e.target)) {
                elements.suggestionsBox.style.display = 'none';
            }
        });
    }

    async function handleFormSubmit(e) {
        e.preventDefault();
        
        const npm = document.getElementById("npm-mahasiswa")?.value.trim();
        const nama = document.getElementById("nama-mahasiswa")?.value.trim();
        const status = document.getElementById("status-mahasiswa")?.value;
        
        if (!nama) {
            showMessage("Nama mahasiswa harus diisi", "error");
            return;
        }
        
        try {
            showLoading(true);
            
            const mahasiswaData = {
                nama: nama,
                status: status || "Aktif"
            };
            
            // Only add NPM if provided
            if (npm) {
                mahasiswaData.npm = npm;
            }
            
            console.log("Sending data:", mahasiswaData);
            const result = await addMahasiswa(mahasiswaData);
            
            if (result.success || result.status === "success") {
                showMessage("Mahasiswa berhasil ditambahkan", "success");
                
                // Reset form
                if (elements.tambahForm) elements.tambahForm.reset();
                
                // Reload data
                await loadMahasiswaData();
                
                // Return to main view
                showMainView();
            }
        } catch (error) {
            console.error("Form submission error:", error);
            showMessage("Gagal menambahkan mahasiswa: " + error.message, "error");
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
        
        const filtered = daftarMahasiswa.filter(mahasiswa => 
            mahasiswa.nama.toLowerCase().includes(keyword) || 
            (mahasiswa.npm && mahasiswa.npm.toLowerCase().includes(keyword))
        );
        
        if (filtered.length === 0) return;
        
        elements.suggestionsBox.style.display = 'block';
        
        filtered.forEach(mahasiswa => {
            const li = document.createElement('li');
            li.textContent = `${mahasiswa.npm || mahasiswa.id} - ${mahasiswa.nama}`;
            li.dataset.id = mahasiswa.id;
            li.dataset.nama = mahasiswa.nama;
            
            li.addEventListener('click', () => {
                if (elements.searchInput) {
                    elements.searchInput.value = `${mahasiswa.npm || mahasiswa.id} - ${mahasiswa.nama}`;
                }
                selectedMahasiswa = {
                    id: mahasiswa.id,
                    npm: mahasiswa.npm,
                    nama: mahasiswa.nama
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
        
        daftarMahasiswa.forEach(mahasiswa => {
            const li = document.createElement('li');
            li.textContent = `${mahasiswa.npm || mahasiswa.id} - ${mahasiswa.nama}`;
            li.dataset.id = mahasiswa.id;
            li.dataset.nama = mahasiswa.nama;
            
            li.addEventListener('click', () => {
                if (elements.searchInput) {
                    elements.searchInput.value = `${mahasiswa.npm || mahasiswa.id} - ${mahasiswa.nama}`;
                }
                selectedMahasiswa = {
                    id: mahasiswa.id,
                    npm: mahasiswa.npm,
                    nama: mahasiswa.nama
                };
                elements.suggestionsBox.style.display = 'none';
            });
            
            elements.suggestionsBox.appendChild(li);
        });
    }

    function handleConfirmDelete() {
        if (!selectedMahasiswa) {
            showMessage("Pilih mahasiswa terlebih dahulu", "error");
            return;
        }
        
        // Update confirmation text
        const confirmText = document.getElementById("konfirmasi-hapus-text");
        if (confirmText) {
            confirmText.innerHTML = `
                Apakah Anda yakin ingin menghapus mahasiswa:<br>
                <strong>${selectedMahasiswa.npm} - ${selectedMahasiswa.nama}</strong>
            `;
        }
        
        showKonfirmasiHapusView();
    }

    async function handleFinalDelete() {
        if (!selectedMahasiswa) {
            showMessage("Tidak ada mahasiswa yang dipilih", "error");
            return;
        }
        
        try {
            showLoading(true);
            
            const result = await deleteMahasiswa(selectedMahasiswa.id);
            
            if (result.success || result.status === "success") {
                showMessage("Mahasiswa berhasil dihapus", "success");
                
                // Reload data
                await loadMahasiswaData();
                
                // Return to main view
                showMainView();
            }
        } catch (error) {
            console.error("Delete error:", error);
            showMessage("Gagal menghapus mahasiswa: " + error.message, "error");
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
            
            const result = await importMahasiswaFile(file);
            
            if (result.success || result.status === "success") {
                showMessage(result.message || "File berhasil diimport", "success");
                
                // Reload data
                await loadMahasiswaData();
                
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
                renderMahasiswaTable();
                return;
            }
            
            const filtered = daftarMahasiswa.filter(mahasiswa => 
                mahasiswa.nama.toLowerCase().includes(keyword) || 
                (mahasiswa.npm && mahasiswa.npm.toLowerCase().includes(keyword)) ||
                (mahasiswa.email && mahasiswa.email.toLowerCase().includes(keyword))
            );
            
            // Render filtered results
            renderFilteredTable(filtered);
        });
    }

    function renderFilteredTable(filteredData) {
        if (!elements.tableView) return;
        
        // Clear existing data rows
        const existingRows = elements.tableView.querySelectorAll('.data-row');
        existingRows.forEach(row => {
            if (row.parentNode) {
                row.parentNode.removeChild(row);
            }
        });
        
        // Add filtered rows
        filteredData.forEach((mahasiswa, index) => {
            const row = document.createElement('div');
            row.className = 'data-row';
            
            const statusText = mahasiswa.status || (mahasiswa.isActive ? 'Aktif' : 'Nonaktif');
            const statusClass = statusText === 'Aktif' ? 'active' : 'inactive';
            
            row.innerHTML = `
                <span>${index + 1}</span>
                <span>${mahasiswa.npm || mahasiswa.id || '-'}</span>
                <span>${mahasiswa.nama || '-'}</span>
                <span>${mahasiswa.email || '-'}</span>
                <span>
                    <span class="status-badge ${statusClass}">
                        ${statusText}
                    </span>
                </span>
            `;
            
            elements.tableView.appendChild(row);
        });
        
        if (filteredData.length === 0) {
            const emptyRow = document.createElement('div');
            emptyRow.className = 'data-row';
            emptyRow.style.textAlign = 'center';
            emptyRow.style.padding = '20px';
            emptyRow.style.color = '#666';
            emptyRow.innerHTML = `
                <span style="grid-column: 1 / -1">Tidak ditemukan data mahasiswa</span>
            `;
            elements.tableView.appendChild(emptyRow);
        }
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
});



const masterPageItems = Array.from(document.querySelectorAll(".data-row"));

// Jika tidak ada data, stop script
if (masterPageItems.length === 0) {
    console.warn("Tidak ada .data-row ditemukan.");
}

// Variabel untuk menyimpan item yang sedang difilter
let filteredPageItems = masterPageItems;

// Elemen Pagination
const prevButton = document.getElementById('prev-page');
const nextButton = document.getElementById('next-page');
const pageInfoSpan = document.getElementById('current-page');

const itemsPerPage = 3;
let currentPage = 1;

let totalPages = Math.max(1, Math.ceil(filteredPageItems.length / itemsPerPage));

// Fungsi Menampilkan Halaman 
const showPage = (page) => {

    const start = (page - 1) * itemsPerPage;
    const end = page * itemsPerPage;

    masterPageItems.forEach(item => {
        item.style.display = "none";
    });

    // Show item filter only untuk halaman ini
    filteredPageItems.forEach((item, index) => {
        if (index >= start && index < end) {
            item.style.display = "block";
        }
    });

    // Update info halaman
    pageInfoSpan.textContent = `${currentPage} / ${totalPages}`;

    // Disable prev/next kalau mentok
    prevButton.disabled = currentPage === 1;
    nextButton.disabled = currentPage === totalPages;
};

// Tombol Prev
prevButton.addEventListener("click", () => {
    if (currentPage > 1) {
        currentPage--;
        showPage(currentPage);
    }
});

// Tombol Next
nextButton.addEventListener("click", () => {
    if (currentPage < totalPages) {
        currentPage++;
        showPage(currentPage);
    }
});


showPage(currentPage);

// Search dan Filter

const searchInput = document.getElementById("search-input");

if (searchInput) {
    searchInput.addEventListener("input", () => {
        const query = searchInput.value.toLowerCase();

        // Filter berdasar span ke-2 (nama) dan span ke-3 (kelas)
        filteredPageItems = masterPageItems.filter(row => {
            const spans = row.querySelectorAll("span");

            const nama = (spans[1]?.textContent || "").toLowerCase();
            const kelas = (spans[2]?.textContent || "").toLowerCase();

            return nama.includes(query) || kelas.includes(query);
        });

        // Hitung ulang total halaman
        totalPages = Math.max(1, Math.ceil(filteredPageItems.length / itemsPerPage));
        currentPage = 1;

        showPage(currentPage);
    });
}
