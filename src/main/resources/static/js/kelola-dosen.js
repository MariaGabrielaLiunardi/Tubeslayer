
    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle');

    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }

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

    const API_BASE_URL = window.API_BASE_URL || window.location.origin;
    const API_DOSEN = window.API_DOSEN || `${API_BASE_URL}/admin/api/dosen`;
    
    console.log("Using API:", API_DOSEN);

    const elements = {

        home: document.querySelector('.home'),
        
        tableView: document.getElementById("table-view"),
        footerView: document.getElementById("footer-view"),
        searchbar: document.getElementById("search-bar"),
        
        pilihCara: document.getElementById("pilih-cara"),
        importView: document.getElementById("import-dosen"),
        manualView: document.getElementById("tambah-dosen"),
        
        hapusView: document.getElementById("view-hapus-dosen"),
        konfirmasiHapus: document.getElementById("konfirmasi-hapus"),
        
        btnAdd: document.getElementById("btn-add"),
        btnDelete: document.getElementById("btn-delete"),
        btnImport: document.getElementById("btn-import"),
        btnManual: document.getElementById("btn-manual"),
        buttonPage: document.getElementById("pagination"),
        
        tambahForm: document.getElementById("tambah-dosen-form"),
        
        listTitle: document.getElementById("list-title"),
        subTitle: document.getElementById("sub-title"),
        subTitle2: document.getElementById("sub-title-2"),
        
        suggestionsBox: document.getElementById("suggestions"),
        searchInput: document.getElementById("search-input"),
        
        btnCancelDelete: document.getElementById("btn-cancel-delete"),
        btnConfirmDelete: document.getElementById("btn-confirm-delete"),
        btnCancelConfirm: document.getElementById("btn-cancel-confirm"),
        btnConfirmDeleteFinal: document.getElementById("btn-confirm-delete-final"),
        btnPilihFile: document.getElementById("btn-pilih-file"),
        fileInput: document.getElementById("file-input")
    };

    console.log("Elements found:");
    Object.keys(elements).forEach(key => {
        console.log(`- ${key}:`, elements[key] ? "✓" : "✗");
    });

    let daftarDosen = [];
    let selectedDosen = null;

    init();

    async function init() {

        hideAllViews();
        showMainView();
        
        setupEventListeners();
        
        await loadDosenData();
        
        setupSearch();
    }

    function hideAllViews() {

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
        
        if (elements.tableView) elements.tableView.style.display = 'none';
        if (elements.footerView) elements.footerView.style.display = 'none';
        if (elements.searchbar) elements.searchbar.style.display = 'none';
        
        if (elements.subTitle) elements.subTitle.textContent = '';
        if (elements.subTitle2) elements.subTitle2.textContent = '';
    }

    function showMainView() {
        console.log("Showing main view");
        
        hideAllViews();
        
        if (elements.tableView) {
            elements.tableView.style.display = 'block';
        }
        if (elements.footerView) {
            elements.footerView.style.display = 'flex';
        }
        if (elements.searchbar) {
            elements.searchbar.style.display = 'block';
        }
        elements.buttonPage.style.display = 'flex';

        if (elements.subTitle) elements.subTitle.textContent = '';
        if (elements.subTitle2) elements.subTitle2.textContent = '';
        
        selectedDosen = null;
        
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
        elements.buttonPage.style.display = 'none';
    }

    function showImportView() {
        console.log("Showing import view");
        hideAllViews();
        if (elements.importView) {
            elements.importView.style.display = 'flex';
        }
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Dosen";
        if (elements.subTitle2) elements.subTitle2.textContent = " > Import";
        elements.buttonPage.style.display = 'none';
    }

    function showManualView() {
        console.log("Showing manual view");
        hideAllViews();
        if (elements.manualView) {
            elements.manualView.style.display = 'flex';
        }
        if (elements.subTitle) elements.subTitle.textContent = " > Tambah Dosen";
        if (elements.subTitle2) elements.subTitle2.textContent = " > Tambah Baru";
        elements.buttonPage.style.display = 'none';
    }

    function showHapusView() {
        console.log("Showing hapus view");
        hideAllViews();
        if (elements.hapusView) {
            elements.hapusView.style.display = 'flex';
        }
        if (elements.subTitle) elements.subTitle.textContent = " > Hapus Dosen";
        if (elements.subTitle2) elements.subTitle2.textContent = "";
        elements.buttonPage.style.display = 'none';
        
        setupDeleteSearch();
    }

    function showKonfirmasiHapusView() {
        console.log("Showing konfirmasi hapus view");
        hideAllViews();
        if (elements.konfirmasiHapus) {
            elements.konfirmasiHapus.style.display = 'flex';
        }
        elements.buttonPage.style.display = 'none';
    }

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
                renderDosenTable();
                updateCount(result.count || daftarDosen.length);
                console.log(`Loaded ${daftarDosen.length} dosen`);
            } else {
                throw new Error(result.message);
            }
        } catch (error) {
            console.error("Error loading data:", error);
            showMessage("Gagal memuat data: " + error.message, "error");
            
            extractDataFromExistingTable();
        } finally {
            showLoading(false);
        }
    }

    async function addDosen(dosenData) {
        try {
            const response = await fetch(API_DOSEN, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
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
                method: 'DELETE'
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

    function renderDosenTable() {
        if (!elements.tableView) {
            console.error("Table view element not found!");
            return;
        }
        
        const existingRows = elements.tableView.querySelectorAll('.data-row');
        existingRows.forEach(row => {
            if (row.parentNode) {
                row.parentNode.removeChild(row);
            }
        });
        
        console.log("Rendering", daftarDosen.length, "dosen");
        
        daftarDosen.forEach((dosen, index) => {
            const row = document.createElement('div');
            row.className = 'data-row';
            
            const statusText = dosen.status || (dosen.isActive ? 'Aktif' : 'Nonaktif');
            const statusClass = statusText === 'Aktif' ? 'active' : 'inactive';
            
            row.innerHTML = `
                <span>${index + 1}.</span>
                <span>${dosen.nip || dosen.id}</span>
                <span>${dosen.nama}</span>
                <span>-</span>
                <span>
                    <span class="status-badge ${statusClass}">
                        ${statusText}
                    </span>
                </span>
            `;
            
            elements.tableView.appendChild(row);
        });
    }

    function extractDataFromExistingTable() {
        if (!elements.tableView) return;
        
        const rows = elements.tableView.querySelectorAll('.data-row');
        daftarDosen = [];
        
        rows.forEach((row, index) => {
            const cells = row.querySelectorAll('span');
            if (cells.length >= 5) {
                const nama = cells[2].textContent.trim();
                if (nama && nama !== 'Nama Dosen') {
                    daftarDosen.push({
                        id: cells[1].textContent.trim() || `DSN${index + 1}`,
                        nip: cells[1].textContent.trim(),
                        nama: nama,
                        status: cells[4].textContent.trim(),
                        isActive: cells[4].textContent.trim() === 'Aktif'
                    });
                }
            }
        });
        
        updateCount(daftarDosen.length);
        console.log(`Extracted ${daftarDosen.length} dosen from existing table`);
    }

    function updateCount(count) {
        if (!elements.listTitle) return;
        
        let countElement = elements.listTitle.querySelector('#dosen-count');
        if (countElement) {
            countElement.textContent = `(${count} data)`;
        } else {

            countElement = document.createElement('span');
            countElement.id = 'dosen-count';
            countElement.style.cssText = 'font-size: 0.8em; color: #666; margin-left: 8px; font-weight: normal;';
            countElement.textContent = `(${count} data)`;
            elements.listTitle.appendChild(countElement);
        }
    }

    function setupEventListeners() {
        console.log("Setting up event listeners");
        
        if (elements.btnAdd) {
            elements.btnAdd.addEventListener('click', showPilihCaraView);
        }
        
        if (elements.btnDelete) {
            elements.btnDelete.addEventListener('click', showHapusView);
        }
        
        if (elements.btnImport) {
            elements.btnImport.addEventListener('click', showImportView);
        }
        
        if (elements.btnManual) {
            elements.btnManual.addEventListener('click', showManualView);
        }
        
        if (elements.tambahForm) {
            elements.tambahForm.addEventListener('submit', handleFormSubmit);
        }
        
        if (elements.btnPilihFile) {
            elements.btnPilihFile.addEventListener('click', () => {
                if (elements.fileInput) elements.fileInput.click();
            });
        }
        
        if (elements.fileInput) {
            elements.fileInput.addEventListener('change', handleFileUpload);
        }
        
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
        
        if (elements.listTitle) {
            elements.listTitle.addEventListener('click', showMainView);
        }
        
        if (elements.searchInput) {
            elements.searchInput.addEventListener('input', handleDeleteSearch);
        }
        
        console.log("Event listeners setup complete");
    }

    function setupSearch() {
        const searchInput = elements.searchbar?.querySelector('input');
        if (!searchInput) return;
        
        searchInput.addEventListener('input', (e) => {
            const keyword = e.target.value.toLowerCase().trim();
            
            if (!keyword) {
                renderDosenTable();
                return;
            }
            
            const filtered = daftarDosen.filter(dosen => 
                dosen.nama.toLowerCase().includes(keyword) || 
                (dosen.nip && dosen.nip.toLowerCase().includes(keyword)) ||
                (dosen.email && dosen.email.toLowerCase().includes(keyword))
            );
            
            const originalData = daftarDosen;
            daftarDosen = filtered;
            renderDosenTable();
            daftarDosen = originalData;
        });
    }

    function setupDeleteSearch() {
        if (!elements.suggestionsBox) return;
        
        elements.suggestionsBox.innerHTML = '';
        elements.suggestionsBox.style.display = 'none';
        
        daftarDosen.forEach(dosen => {
            const li = document.createElement('li');
            li.textContent = `${dosen.nip} - ${dosen.nama}`;
            li.dataset.id = dosen.id;
            li.dataset.nama = dosen.nama;
            
            li.addEventListener('click', () => {
                if (elements.searchInput) {
                    elements.searchInput.value = `${dosen.nip} - ${dosen.nama}`;
                }
                selectedDosen = {
                    id: dosen.id,
                    nama: dosen.nama
                };
                if (elements.suggestionsBox) {
                    elements.suggestionsBox.style.display = 'none';
                }
            });
            
            elements.suggestionsBox.appendChild(li);
        });
    }

    async function handleFormSubmit(e) {
        e.preventDefault();
        
        const nip = document.getElementById("nip-dosen").value.trim();
        const nama = document.getElementById("nama-dosen").value.trim();
        const status = document.getElementById("status-dosen").value;
        
        if (!nama) {
            showMessage("Nama dosen harus diisi", "error");
            return;
        }
        
        try {
            showLoading(true);
            
            const dosenData = {
                nama: nama,
                status: status
            };
            
            if (nip) {
                dosenData.nip = nip;
            }
            
            const result = await addDosen(dosenData);
            
            if (result.success) {
                showMessage(result.message, "success");
                
                elements.tambahForm.reset();
                
                await loadDosenData();
                
                showMainView();
            }
        } catch (error) {
            console.error("Form submission error:", error);
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
            dosen.nama.toLowerCase().includes(keyword) || 
            (dosen.nip && dosen.nip.toLowerCase().includes(keyword))
        );
        
        if (filtered.length === 0) return;
        
        elements.suggestionsBox.style.display = 'block';
        
        filtered.forEach(dosen => {
            const li = document.createElement('li');
            li.textContent = `${dosen.nip} - ${dosen.nama}`;
            li.dataset.id = dosen.id;
            li.dataset.nama = dosen.nama;
            
            li.addEventListener('click', () => {
                if (elements.searchInput) {
                    elements.searchInput.value = `${dosen.nip} - ${dosen.nama}`;
                }
                selectedDosen = {
                    id: dosen.id,
                    nama: dosen.nama
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
                showMessage(result.message, "success");
                
                await loadDosenData();
                
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
        
        const validTypes = ['text/csv', 'application/vnd.ms-excel', 
                           'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'];
        
        if (!validTypes.includes(file.type) && !file.name.match(/\.(csv|xlsx|xls)$/)) {
            showMessage("File harus berupa CSV atau Excel", "error");
            e.target.value = '';
            return;
        }
        
        try {
            showLoading(true);
            
            const formData = new FormData();
            formData.append('file', file);
            
            const response = await fetch(`${API_DOSEN}/import`, {
                method: 'POST',
                body: formData
            });
            
            const result = await response.json();
            
            if (result.success) {
                showMessage(result.message, "success");
                
                await loadDosenData();
                
                showMainView();
            } else {
                throw new Error(result.message);
            }
        } catch (error) {
            console.error("File upload error:", error);
            showMessage("Gagal mengimpor file: " + error.message, "error");
        } finally {
            showLoading(false);
            e.target.value = '';
        }
    }

    function showLoading(show) {
        const loadingEl = document.getElementById('global-loading');
        if (loadingEl) {
            loadingEl.style.display = show ? 'flex' : 'none';
        }
    }

    function showMessage(message, type = 'info') {

        const existing = document.querySelectorAll('.notification');
        existing.forEach(el => el.remove());
        
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <div style="position: fixed; top: 20px; right: 20px; padding: 12px 20px; 
                        border-radius: 4px; color: white; z-index: 10000; font-size: 14px;
                        background: ${type === 'success' ? '#4CAF50' : type === 'error' ? '#f44336' : '#2196F3'};
                        box-shadow: 0 2px 5px rgba(0,0,0,0.2); display: flex; align-items: center; gap: 10px;">
                ${type === 'success' ? '✓' : type === 'error' ? '✗' : 'ℹ'} ${message}
            </div>
        `;
        
        document.body.appendChild(notification);
        
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 3000);
    }

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

const masterPageItems = Array.from(document.querySelectorAll("#table-view .data-row"));

if (masterPageItems.length === 0) {
    console.warn("Tidak ada .data-row ditemukan.");
}

let filteredPageItems = masterPageItems;

const prevButton = document.getElementById('prev-page');
const nextButton = document.getElementById('next-page');
const pageInfoSpan = document.getElementById('current-page');

const itemsPerPage = 3;
let currentPage = 1;

let totalPages = Math.max(1, Math.ceil(filteredPageItems.length / itemsPerPage));

const showPage = (page) => {

    const start = (page - 1) * itemsPerPage;
    const end = page * itemsPerPage;

    masterPageItems.forEach(item => {
        item.style.display = "none";
    });

    filteredPageItems.forEach((item, index) => {
        if (index >= start && index < end) {
            item.style.display = "block";
        }
    });

    pageInfoSpan.textContent = `${currentPage} / ${totalPages}`;

    prevButton.disabled = currentPage === 1;
    nextButton.disabled = currentPage === totalPages;
};

prevButton.addEventListener("click", () => {
    if (currentPage > 1) {
        currentPage--;
        showPage(currentPage);
    }
});

nextButton.addEventListener("click", () => {
    if (currentPage < totalPages) {
        currentPage++;
        showPage(currentPage);
    }
});

showPage(currentPage);

const searchInput = document.getElementById("search-input");

if (searchInput) {
    searchInput.addEventListener("input", () => {
        const query = searchInput.value.toLowerCase();

        filteredPageItems = masterPageItems.filter(row => {
            const spans = row.querySelectorAll("span");

            const nama = (spans[1]?.textContent || "").toLowerCase();
            const kelas = (spans[2]?.textContent || "").toLowerCase();

            return nama.includes(query) || kelas.includes(query);
        });

        totalPages = Math.max(1, Math.ceil(filteredPageItems.length / itemsPerPage));
        currentPage = 1;

        showPage(currentPage);
    });
}