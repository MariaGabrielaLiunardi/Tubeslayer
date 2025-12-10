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
    const API_BASE_URL = window.API_BASE_URL || window.location.origin || "http://localhost:8080";
    const API_MATA_KULIAH = window.API_MATKUL || window.API_MATA_KULIAH || `${API_BASE_URL}/admin/api/mata-kuliah`;
    console.log("API Mata Kuliah:", API_MATA_KULIAH);

    // ==================== ELEMENTS ====================
    const elements = {
        // Main views
        tableView: document.getElementById("table-view"),
        tableBody: document.getElementById("table-body"),
        footerView: document.getElementById("footer-view"),
        searchbar: document.getElementById("search-bar"),
        paginationContainer: document.querySelector(".pagination-container"),
        
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
        fileInput: document.getElementById("file-input"),

        // Pagination
        prevPageBtn: document.getElementById("prev-page"),
        nextPageBtn: document.getElementById("next-page"),
        pageInfo: document.getElementById("page-info")
    };

    // ==================== STATE ====================
    let daftarMataKuliah = [];
    let selectedMataKuliah = null;
    let filteredData = null;
    let currentPage = 1;
    const pageSize = 10;

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
        if (elements.paginationContainer) elements.paginationContainer.style.display = 'flex';
        
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
        if (elements.paginationContainer) elements.paginationContainer.style.display = 'none';
    }

    function showImportView() {
        hideAllViews();
        if (elements.importView) elements.importView.style.display = 'flex';
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Mata Kuliah";
        if (elements.subTitle2) elements.subTitle2.textContent = " > Import Data";

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
        if (elements.paginationContainer) elements.paginationContainer.style.display = 'none';
    }

    function showManualView() {
        hideAllViews();
        if (elements.manualView) elements.manualView.style.display = 'flex';
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Mata Kuliah";
        if (elements.subTitle2) elements.subTitle2.textContent = " > Tambah Baru";

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
        if (elements.paginationContainer) elements.paginationContainer.style.display = 'none';
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
        if (elements.paginationContainer) elements.paginationContainer.style.display = 'none';
    }

    function showKonfirmasiHapusView() {
        hideAllViews();
        if (elements.konfirmasiHapus) elements.konfirmasiHapus.style.display = 'flex';

        elements.tableView.style.display = 'none';
        elements.footerView.style.display = 'none';
        elements.searchbar.style.display = 'none';
        if (elements.paginationContainer) elements.paginationContainer.style.display = 'none';
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
                filteredData = null;
                currentPage = 1;
                renderMataKuliahTable();
                updateCount(daftarMataKuliah.length);
                console.log(`✅ Loaded ${daftarMataKuliah.length} mata kuliah`);
            } else {
                throw new Error(result.message || "Failed to load data");
            }
        } catch (error) {
            console.error("❌ Error loading data:", error);
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
function getActiveData() {
    return filteredData ?? daftarMataKuliah;
}

function updatePaginationInfo(totalPages) {
    if (elements.pageInfo) {
        elements.pageInfo.textContent = `${totalPages === 0 ? 0 : currentPage} / ${Math.max(totalPages, 1)}`;
    }
}

function renderMataKuliahTable() {
    if (!elements.tableBody) {
        console.error("Table body element not found!");
        return;
    }

    const data = getActiveData();
    const totalPages = Math.ceil(data.length / pageSize) || 1;
    if (currentPage > totalPages) currentPage = totalPages;
    if (currentPage < 1) currentPage = 1;

    const start = (currentPage - 1) * pageSize;
    const pageItems = data.slice(start, start + pageSize);

    elements.tableBody.innerHTML = "";

    if (pageItems.length === 0) {
        const emptyRow = document.createElement("tr");
        const td = document.createElement("td");
        td.colSpan = 5;
        td.style.textAlign = "center";
        td.textContent = "Tidak ada data mata kuliah";
        emptyRow.appendChild(td);
        elements.tableBody.appendChild(emptyRow);
        updatePaginationInfo(0);
        return;
    }

    pageItems.forEach((mk, idx) => {
        const tr = document.createElement("tr");
        tr.className = "data-row";

        const statusText = mk.status || (mk.active ? 'Aktif' : 'Nonaktif') || 'Aktif';
        const statusClass = statusText === 'Aktif' ? 'active' : 'inactive';

        tr.innerHTML = `
            <td>${start + idx + 1}</td>
            <td>${mk.kodeMatkul || mk.kodeMK || mk.kode || mk.id || '-'}</td>
            <td>${mk.namaMatkul || mk.nama || '-'}</td>
            <td>${mk.sks ?? '-'}</td>
            <td><span class="status-badge ${statusClass}">${statusText}</span></td>
        `;
        elements.tableBody.appendChild(tr);
    });

    updatePaginationInfo(totalPages);
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

        // Pagination
        if (elements.prevPageBtn) {
            elements.prevPageBtn.addEventListener('click', () => {
                if (currentPage > 1) {
                    currentPage -= 1;
                    renderMataKuliahTable();
                }
            });
        }
        if (elements.nextPageBtn) {
            elements.nextPageBtn.addEventListener('click', () => {
                const totalPages = Math.ceil(getActiveData().length / pageSize) || 1;
                if (currentPage < totalPages) {
                    currentPage += 1;
                    renderMataKuliahTable();
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
    }

    async function handleFormSubmit(e) {
        e.preventDefault();
        
        const kode = document.getElementById("kode-mk")?.value.trim();
        const nama = document.getElementById("nama-mk")?.value.trim();
        const sks = document.getElementById("sks-mk")?.value;
        const statusVal = document.getElementById("status-mk")?.value;
        
        if (!kode) return showMessage("Kode mata kuliah harus diisi", "error");
        if (!nama) return showMessage("Nama mata kuliah harus diisi", "error");
        
        try {
            showLoading(true);
            
            const mataKuliahData = {
                kodeMatkul: kode,
                namaMatkul: nama,
                sks: parseInt(sks) || 3,
                active: statusVal !== "0",
                status: statusVal === "0" ? "Nonaktif" : "Aktif"
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
            (mk.namaMatkul || mk.nama || '').toLowerCase().includes(keyword) || 
            (mk.kodeMatkul || mk.kode || '').toLowerCase().includes(keyword)
        );
        
        if (filtered.length === 0) return;
        
        elements.suggestionsBox.style.display = 'block';
        
        filtered.forEach(mk => {
            const li = document.createElement('li');
            const kode = mk.kodeMatkul || mk.kode || mk.id;
            const nama = mk.namaMatkul || mk.nama;
            li.textContent = `${kode} - ${nama}`;
            li.dataset.id = mk.id;
            li.dataset.nama = nama;
            li.dataset.kode = kode;
            
            li.addEventListener('click', () => {
                if (elements.searchInput) {
                    elements.searchInput.value = `${kode} - ${nama}`;
                }
                selectedMataKuliah = {
                    id: mk.id,
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
        
        daftarMataKuliah.forEach(mk => {
            const li = document.createElement('li');
            const kode = mk.kodeMatkul || mk.kode || mk.id;
            const nama = mk.namaMatkul || mk.nama;
            li.textContent = `${kode} - ${nama}`;
            li.dataset.id = mk.id;
            li.dataset.nama = nama;
            li.dataset.kode = kode;
            
            li.addEventListener('click', () => {
                if (elements.searchInput) {
                    elements.searchInput.value = `${kode} - ${nama}`;
                }
                selectedMataKuliah = {
                    id: mk.id,
                    kode: kode,
                    nama: nama
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
                filteredData = null;
                currentPage = 1;
                renderMataKuliahTable();
                return;
            }
            
            filteredData = daftarMataKuliah.filter(mk => 
                (mk.namaMatkul || mk.nama || '').toLowerCase().includes(keyword) || 
                (mk.kodeMatkul || mk.kode || '').toLowerCase().includes(keyword)
            );
            currentPage = 1;
            renderMataKuliahTable();
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


// PAGINATION SYSTEM
let currentPage = 1;
let pageSize = 3; // jumlah data per halaman
let totalPages = 0;
let allData = []; // Menyimpan semua data asli
let filteredData = []; // Data setelah filter/search
let masterDataRows = []; // Array untuk menyimpan semua row DOM elements
let filteredPageItems = []; // Array untuk row yang akan ditampilkan

// DOM Elements
const pesertaCountSpan = document.getElementById("peserta-count");
const prevButton = document.getElementById("prev-page");
const nextButton = document.getElementById("next-page");
const pageInfoSpan = document.getElementById("page-info");
const noDataRow = document.querySelector(".no-data-row"); // Pastikan ada di HTML

// Initialize data
function initializeData(data) {
    try {
        allData = Array.isArray(data) ? data : [];
        filteredData = [...allData];
        
        // Inisialisasi masterDataRows dari tabel yang sudah ada
        initializeTableRows();
        
        // Update pagination
        updateFilteredItems();
        updateUI(true);
        
        console.log(`Data initialized: ${allData.length} items`);
    } catch (error) {
        console.error("Error initializing data:", error);
    }
}

// Inisialisasi rows dari tabel
function initializeTableRows() {
    const tableBody = document.querySelector(".custom-table-container");
    if (!tableBody) {
        console.error("Table container not found!");
        return;
    }
    
    // Ambil semua data rows (kecuali header dan no-data row)
    const allRows = tableBody.querySelectorAll(".data-row");
    masterDataRows = Array.from(allRows);
    
    // Cari no-data row jika ada
    noDataRow = tableBody.querySelector(".no-data-row") || 
                 document.querySelector(".no-data-row");
    
    console.log(`Found ${masterDataRows.length} data rows`);
}

// Update filtered items berdasarkan data yang difilter
function updateFilteredItems() {
    if (!masterDataRows.length) {
        console.warn("No master data rows available");
        filteredPageItems = [];
        return;
    }
    
    // Jika ada filteredData, sesuaikan rows yang ditampilkan
    if (filteredData.length > 0 && filteredData.length <= masterDataRows.length) {
        // Sembunyikan semua rows terlebih dahulu
        masterDataRows.forEach(row => {
            if (row.style) row.style.display = "none";
        });
        
        // Tampilkan hanya rows sesuai dengan filteredData
        filteredPageItems = [];
        filteredData.forEach((item, index) => {
            if (index < masterDataRows.length) {
                filteredPageItems.push(masterDataRows[index]);
            }
        });
    } else {
        // Jika tidak ada filter, gunakan semua rows
        filteredPageItems = [...masterDataRows];
    }
    
    console.log(`Filtered items: ${filteredPageItems.length} rows`);
}

// Setup event listeners untuk pagination
function setupPaginationListeners() {
    if (prevButton) {
        prevButton.addEventListener("click", () => {
            if (currentPage > 1) {
                currentPage--;
                updateUI();
            }
        });
    }
    
    if (nextButton) {
        nextButton.addEventListener("click", () => {
            if (currentPage < totalPages) {
                currentPage++;
                updateUI();
            }
        });
    }
}

// Tampilkan halaman tertentu
function showPage(page) {
    const start = (page - 1) * pageSize;
    const end = page * pageSize;
    
    // Sembunyikan semua rows terlebih dahulu
    masterDataRows.forEach(row => {
        if (row && row.style) {
            row.style.display = "none";
        }
    });
    
    // Tampilkan rows untuk halaman ini
    filteredPageItems.forEach((row, index) => {
        if (index >= start && index < end && row && row.style) {
            row.style.display = "table-row";
        }
    });
    
    // Jika tidak ada data, tampilkan no-data row
    if (filteredPageItems.length === 0 && noDataRow) {
        noDataRow.style.display = "table-row";
    } else if (noDataRow) {
        noDataRow.style.display = "none";
    }
}

// Update UI pagination
function updateUI(resetPage = false) {
    if (resetPage) {
        currentPage = 1;
    }
    
    // Hitung total pages
    totalPages = Math.max(1, Math.ceil(filteredPageItems.length / pageSize));
    
    // Pastikan currentPage tidak melebihi totalPages
    if (currentPage > totalPages) {
        currentPage = totalPages;
    }
    
    // Update page info
    if (pageInfoSpan) {
        pageInfoSpan.textContent = `${currentPage} dari ${totalPages}`;
    }
    
    // Update peserta count
    if (pesertaCountSpan) {
        pesertaCountSpan.textContent = `Peserta: ${filteredData.length} peserta`;
    }
    
    // Update button states
    updatePaginationButtons();
    
    // Tampilkan halaman saat ini
    showPage(currentPage);
}

// Update state pagination buttons
function updatePaginationButtons() {
    if (prevButton) {
        const isDisabled = currentPage <= 1;
        prevButton.disabled = isDisabled;
        prevButton.classList.toggle("disabled", isDisabled);
    }
    
    if (nextButton) {
        const isDisabled = currentPage >= totalPages;
        nextButton.disabled = isDisabled;
        nextButton.classList.toggle("disabled", isDisabled);
    }
}

// Filter data berdasarkan keyword
function filterData(keyword) {
    if (!keyword || keyword.trim() === "") {
        // Reset ke semua data
        filteredData = [...allData];
    } else {
        const searchTerm = keyword.toLowerCase().trim();
        filteredData = allData.filter(item => {
            // Sesuaikan dengan struktur data Anda
            const nama = (item.nama || "").toLowerCase();
            const npm = (item.npm || item.id || "").toLowerCase();
            return nama.includes(searchTerm) || npm.includes(searchTerm);
        });
    }
    
    updateFilteredItems();
    updateUI(true);
    
    console.log(`Filtered to ${filteredData.length} items`);
}

// Change page size
function changePageSize(newSize) {
    pageSize = parseInt(newSize) || 3;
    updateUI(true);
}

// Inisialisasi pagination system
function initPagination() {
    setupPaginationListeners();
    initializeTableRows();
    updateUI(true);
    
    console.log("Pagination system initialized");
}

// Panggil init saat halaman dimuat
document.addEventListener("DOMContentLoaded", () => {
    initPagination();
    
    // Contoh data dummy jika belum ada data
    if (allData.length === 0) {
        // Coba ekstrak data dari tabel yang sudah ada
        extractDataFromTable();
    }
});

// Fungsi untuk mengekstrak data dari tabel yang sudah ada
function extractDataFromTable() {
    if (!masterDataRows.length) {
        initializeTableRows();
    }
    
    // Ekstrak data dari setiap row
    allData = masterDataRows.map((row, index) => {
        const cells = row.querySelectorAll("td, span");
        return {
            id: index + 1,
            nama: cells[0]?.textContent || `Peserta ${index + 1}`,
            npm: cells[1]?.textContent || `NPM${index + 1}`,
            // tambahkan properti lain sesuai kebutuhan
        };
    });
    
    filteredData = [...allData];
    updateFilteredItems();
    updateUI(true);
    
    console.log(`Extracted ${allData.length} items from table`);
}