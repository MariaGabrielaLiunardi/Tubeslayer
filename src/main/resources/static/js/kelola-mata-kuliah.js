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
    console.log("Kelola Mata Kuliah JS Loaded");

    // ==================== CONFIGURATION ====================
    const API_BASE_URL = window.API_BASE_URL || "http://localhost:8080";
    const API_MATA_KULIAH = window.API_MATA_KULIAH || `${API_BASE_URL}/admin/api/mata-kuliah`;
    console.log("API Mata Kuliah:", API_MATA_KULIAH);

    // ==================== ELEMENTS ====================
    const elements = {
        // Main views
        tableView: document.getElementById("table-view"),
        footerView: document.getElementById("footer-view"),
        searchbar: document.getElementById("search-bar"),
        
        // Add mata kuliah flow
        pilihCara: document.getElementById("pilih-cara"),
        importView: document.getElementById("import-matkul"),
        manualView: document.getElementById("tambah-matkul"),
        
        // Delete mata kuliah flow
        hapusView: document.getElementById("hapus-matkul"),
        konfirmasiHapus: document.getElementById("konfirmasi-hapus"),
        
        // Buttons
        btnAdd: document.getElementById("btn-add"),
        btnDelete: document.getElementById("btn-delete"),
        btnImport: document.getElementById("btn-import"),
        btnManual: document.getElementById("btn-manual"),
        
        // Forms
        tambahForm: document.getElementById("tambah-matkul-form"),
        
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
        fileInput: document.getElementById("file-input")
    };

    // ==================== STATE ====================
    let daftarMataKuliah = [];
    let selectedMataKuliah = null;

    // ==================== INITIALIZATION ====================
    init();

    async function init() {
        
        showMainView();

        // Setup event listeners
        setupEventListeners();
        
        // Load initial data
        await loadMataKuliahData();
        
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
        
        // Clear titles
        if (elements.subTitle) elements.subTitle.textContent = '';
        if (elements.subTitle2) elements.subTitle2.textContent = '';
        
        // Clear selected
        selectedMataKuliah = null;
        
        // Reset search input
        if (elements.searchInput) {
            elements.searchInput.value = '';
            elements.searchInput.placeholder = 'Cari mata kuliah...';
        }
        if (elements.suggestionsBox) {
            elements.suggestionsBox.innerHTML = '';
            elements.suggestionsBox.style.display = 'none';
        }
    }

    function showPilihCaraView() {
        hideAllViews();
        if (elements.pilihCara) elements.pilihCara.style.display = 'flex';
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Mata Kuliah";
        if (elements.subTitle2) elements.subTitle2.textContent = "";

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
    }

    function showImportView() {
        hideAllViews();
        if (elements.importView) elements.importView.style.display = 'flex';
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Mata Kuliah";
        if (elements.subTitle2) elements.subTitle2.textContent = " > Import Data";

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
    }

    function showManualView() {
        hideAllViews();
        if (elements.manualView) elements.manualView.style.display = 'flex';
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Mata Kuliah";
        if (elements.subTitle2) elements.subTitle2.textContent = " > Tambah Baru";

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
    }

    function showHapusView() {
        hideAllViews();
        if (elements.hapusView) elements.hapusView.style.display = 'flex';
        if (elements.subTitle) elements.subTitle.textContent = " > Hapus Mata Kuliah";
        if (elements.subTitle2) elements.subTitle2.textContent = "";
        
        setupDeleteSearch();

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
    }

    function showKonfirmasiHapusView() {
        hideAllViews();
        if (elements.konfirmasiHapus) elements.konfirmasiHapus.style.display = 'flex';

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
    }

    // ==================== API FUNCTIONS ====================
    async function loadMataKuliahData() {
        try {
            showLoading(true);
            console.log("📡 Fetching from:", API_MATA_KULIAH);
            
            const response = await fetch(API_MATA_KULIAH, {
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
                daftarMataKuliah = result.data || [];
                renderMataKuliahTable();
                updateCount(daftarMataKuliah.length);
                console.log(`Loaded ${daftarMataKuliah.length} mata kuliah`);
            } else {
                throw new Error(result.message || "Failed to load data");
            }
        } catch (error) {
            console.error("Error loading data:", error);
            showMessage("Gagal memuat data mata kuliah: " + error.message, "error");
            
            // Fallback: show empty state
            daftarMataKuliah = [];
            renderMataKuliahTable();
            updateCount(0);
        } finally {
            showLoading(false);
        }
    }

    async function addMataKuliah(mataKuliahData) {
        try {
            const response = await fetch(API_MATA_KULIAH, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(mataKuliahData)
            });
            
            const result = await response.json();
            
            if (!response.ok) {
                throw new Error(result.message || `HTTP ${response.status}`);
            }
            
            return result;
        } catch (error) {
            console.error("Error adding mata kuliah:", error);
            throw error;
        }
    }

    async function deleteMataKuliah(id) {
        try {
            const response = await fetch(`${API_MATA_KULIAH}/${id}`, {
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
            console.error("Error deleting mata kuliah:", error);
            throw error;
        }
    }

    async function importMataKuliahFile(file) {
        try {
            const formData = new FormData();
            formData.append('file', file);
            
            const response = await fetch(`${API_MATA_KULIAH}/import`, {
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
// ==================== RENDER FUNCTIONS ====================
function renderMataKuliahTable() {
    if (!elements.tableView) {
        console.error("Table view element not found!");
        return;
    }
    
    // Clear existing data rows
    elements.tableView.innerHTML = '';
    
    console.log("Rendering", daftarMataKuliah.length, "mata kuliah");
    
    if (daftarMataKuliah.length === 0) {
        const emptyState = document.createElement('div');
        emptyState.className = 'empty-state';
        emptyState.innerHTML = `
            <i class="fas fa-book"></i>
            <p>Tidak ada data mata kuliah</p>
        `;
        elements.tableView.appendChild(emptyState);
        return;
    }
    
    // Add new rows (5 KOLOM SAMA DENGAN MAHASISWA)
    daftarMataKuliah.forEach((mataKuliah, index) => {
        const row = document.createElement('div');
        row.className = 'data-row';
        
        const statusText = mataKuliah.status || 'Aktif';
        const statusClass = statusText === 'Aktif' ? 'active' : 'inactive';
        
        // HANYA 5 KOLOM: No, Kode, Nama, SKS, Status
        row.innerHTML = `
            <span>${index + 1}</span>
            <span>${mataKuliah.kode || '-'}</span>
            <span>${mataKuliah.nama || '-'}</span>
            <span>${mataKuliah.sks || '-'} SKS</span>
            <span>
                <span class="status-badge ${statusClass}">
                    ${statusText}
                </span>
            </span>
        `;
        
        elements.tableView.appendChild(row);
    });
}

function renderFilteredTable(filteredData) {
    if (!elements.tableView) return;
    
    let headerRow = elements.tableView.querySelector('.table-header-row');
    
    // Jika header tidak ditemukan, buat baru
    if (!headerRow) {
        headerRow = document.createElement('div');
        headerRow.className = 'table-header-row';
        headerRow.innerHTML = `
            <span>No</span>
            <span>Kode MK</span>
            <span>Nama Mata Kuliah</span>
            <span>SKS</span>
            <span>Status</span>
        `;
    }
    
    // Clear existing data rows saja (jaga header)
    const dataRows = elements.tableView.querySelectorAll('.data-row, .empty-state');
    dataRows.forEach(row => {
        if (row.parentNode) {
            row.parentNode.removeChild(row);
        }
    });
    
    if (filteredData.length === 0) {
        // Tampilkan pesan "data tidak ditemukan"
        const emptyRow = document.createElement('div');
        emptyRow.className = 'empty-state';
        emptyRow.innerHTML = `
            <i class="fas fa-search"></i>
            <p>Tidak ditemukan data mata kuliah</p>
        `;
        
        // Pastikan header ada sebelum menambahkan empty state
        if (!elements.tableView.querySelector('.table-header-row')) {
            elements.tableView.appendChild(headerRow);
        }
        elements.tableView.appendChild(emptyRow);
        return;
    }
    
    // Pastikan header ada
    if (!elements.tableView.querySelector('.table-header-row')) {
        elements.tableView.appendChild(headerRow);
    }
    
    filteredData.forEach((mk, index) => {
        const row = document.createElement('div');
        row.className = 'data-row';
        
        const statusText = mk.status || (mk.isActive ? 'Aktif' : 'Nonaktif');
        const statusClass = statusText === 'Aktif' ? 'active' : 'inactive';
        
        row.innerHTML = `
            <span>${index + 1}</span>
            <span>${mk.kode || mk.id}</span>
            <span>${mk.nama}</span>
            <span>${mk.sks || '-'}</span>
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
        
        let countElement = elements.listTitle.querySelector('#mata-kuliah-count');
        if (countElement) {
            countElement.textContent = `(${count} data)`;
        } else {
            countElement = document.createElement('span');
            countElement.id = 'mata-kuliah-count';
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
        
        // Add mata kuliah flow
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
        
        const kode = document.getElementById("kode-mata-kuliah")?.value.trim();
        const nama = document.getElementById("nama-mata-kuliah")?.value.trim();
        const sks = document.getElementById("sks-mata-kuliah")?.value;
        const semester = document.getElementById("semester-mata-kuliah")?.value;
        
        if (!kode) {
            showMessage("Kode mata kuliah harus diisi", "error");
            return;
        }
        
        if (!nama) {
            showMessage("Nama mata kuliah harus diisi", "error");
            return;
        }
        
        try {
            showLoading(true);
            
            const mataKuliahData = {
                kode: kode,
                nama: nama,
                sks: parseInt(sks) || 3,
                semester: parseInt(semester) || 1,
                status: "Aktif"
            };
            
            console.log("Sending data:", mataKuliahData);
            const result = await addMataKuliah(mataKuliahData);
            
            if (result.success || result.status === "success") {
                showMessage("Mata kuliah berhasil ditambahkan", "success");
                
                // Reset form
                if (elements.tambahForm) elements.tambahForm.reset();
                
                // Reload data
                await loadMataKuliahData();
                
                // Return to main view
                showMainView();
            }
        } catch (error) {
            console.error("Form submission error:", error);
            showMessage("Gagal menambahkan mata kuliah: " + error.message, "error");
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
        
        const filtered = daftarMataKuliah.filter(mk => 
            mk.nama.toLowerCase().includes(keyword) || 
            (mk.kode && mk.kode.toLowerCase().includes(keyword))
        );
        
        if (filtered.length === 0) return;
        
        elements.suggestionsBox.style.display = 'block';
        
        filtered.forEach(mk => {
            const li = document.createElement('li');
            li.textContent = `${mk.kode || mk.id} - ${mk.nama}`;
            li.dataset.id = mk.id;
            li.dataset.nama = mk.nama;
            li.dataset.kode = mk.kode;
            
            li.addEventListener('click', () => {
                if (elements.searchInput) {
                    elements.searchInput.value = `${mk.kode || mk.id} - ${mk.nama}`;
                }
                selectedMataKuliah = {
                    id: mk.id,
                    kode: mk.kode,
                    nama: mk.nama
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
        
        daftarMataKuliah.forEach(mk => {
            const li = document.createElement('li');
            li.textContent = `${mk.kode || mk.id} - ${mk.nama}`;
            li.dataset.id = mk.id;
            li.dataset.nama = mk.nama;
            li.dataset.kode = mk.kode;
            
            li.addEventListener('click', () => {
                if (elements.searchInput) {
                    elements.searchInput.value = `${mk.kode || mk.id} - ${mk.nama}`;
                }
                selectedMataKuliah = {
                    id: mk.id,
                    kode: mk.kode,
                    nama: mk.nama
                };
                elements.suggestionsBox.style.display = 'none';
            });
            
            elements.suggestionsBox.appendChild(li);
        });
    }

    function handleConfirmDelete() {
        if (!selectedMataKuliah) {
            showMessage("Pilih mata kuliah terlebih dahulu", "error");
            return;
        }
        
        // Update confirmation text
        const confirmText = document.getElementById("konfirmasi-hapus-text");
        if (confirmText) {
            confirmText.innerHTML = `
                Apakah Anda yakin ingin menghapus mata kuliah:<br>
                <strong>${selectedMataKuliah.kode} - ${selectedMataKuliah.nama}</strong>
            `;
        }
        
        showKonfirmasiHapusView();
    }

    async function handleFinalDelete() {
        if (!selectedMataKuliah) {
            showMessage("Tidak ada mata kuliah yang dipilih", "error");
            return;
        }
        
        try {
            showLoading(true);
            
            const result = await deleteMataKuliah(selectedMataKuliah.id);
            
            if (result.success || result.status === "success") {
                showMessage("Mata kuliah berhasil dihapus", "success");
                
                // Reload data
                await loadMataKuliahData();
                
                // Return to main view
                showMainView();
            }
        } catch (error) {
            console.error("Delete error:", error);
            showMessage("Gagal menghapus mata kuliah: " + error.message, "error");
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
            
            const result = await importMataKuliahFile(file);
            
            if (result.success || result.status === "success") {
                showMessage(result.message || "File berhasil diimport", "success");
                
                // Reload data
                await loadMataKuliahData();
                
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
                renderMataKuliahTable();
                return;
            }
            
            const filtered = daftarMataKuliah.filter(mk => 
                mk.nama.toLowerCase().includes(keyword) || 
                (mk.kode && mk.kode.toLowerCase().includes(keyword))
            );
            
            // Render filtered results
            renderFilteredTable(filtered);
        });
    }

    function renderFilteredTable(filteredData) {
        if (!elements.tableView) return;
        
        // Clear existing data rows
        elements.tableView.innerHTML = '';
        
        if (filteredData.length === 0) {
            const emptyRow = document.createElement('div');
            emptyRow.className = 'empty-state';
            emptyRow.innerHTML = `
                <i class="fas fa-search"></i>
                <p>Tidak ditemukan data mata kuliah</p>
            `;
            elements.tableView.appendChild(emptyRow);
            return;
        }
        
        // Add filtered rows
        filteredData.forEach((mk, index) => {
            const row = document.createElement('div');
            row.className = 'data-row';
            
            const statusText = mk.status || 'Aktif';
            const statusClass = statusText === 'Aktif' ? 'active' : 'inactive';
            
            row.innerHTML = `
                <span>${index + 1}</span>
                <span>${mk.kode || '-'}</span>
                <span>${mk.nama || '-'}</span>
                <span>${mk.sks || '-'} SKS</span>
                <span>Semester ${mk.semester || '-'}</span>
                <span>
                    <span class="status-badge ${statusClass}">
                        ${statusText}
                    </span>
                </span>
            `;
            
            elements.tableView.appendChild(row);
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
});