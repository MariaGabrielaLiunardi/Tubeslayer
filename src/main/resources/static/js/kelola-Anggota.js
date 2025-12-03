const API_BASE_URL = '/api'; // Sesuaikan dengan base URL Spring Boot Anda
const TUGAS_ID = 1; // ID tugas yang sedang diakses (bisa diambil dari URL parameter)

// ===============================
// DATA LAYER - DATABASE INTEGRATION
// ===============================

/**
 * Load semua kelompok untuk tugas tertentu
 */
async function loadKelompokFromDatabase(idTugas) {
    try {
        const response = await fetch(`${API_BASE_URL}/kelompok/tugas/${idTugas}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error loading kelompok:', error);
        throw error;
    }
}

/**
 * Simpan kelompok baru ke database
 * @param {Object} kelompok - Data kelompok yang akan disimpan
 */
async function saveKelompokToDatabase(kelompok) {
    try {
        const response = await fetch(`${API_BASE_URL}/kelompok`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(kelompok)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error saving kelompok:', error);
        throw error;
    }
}

/**
 * Hapus kelompok dari database
 * @param {number} idKelompok - ID kelompok yang akan dihapus
 */
async function deleteKelompokFromDatabase(idKelompok) {
    try {
        const response = await fetch(`${API_BASE_URL}/kelompok/${idKelompok}`, {
            method: 'POST', // Menggunakan POST karena requirement
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error deleting kelompok:', error);
        throw error;
    }
}

/**
 * Finalisasi semua kelompok untuk tugas tertentu
 * @param {number} idTugas - ID tugas yang akan difinalisasi
 */
async function finalisasiKelompokToDatabase(idTugas) {
    try {
        const response = await fetch(`${API_BASE_URL}/kelompok/finalisasi/${idTugas}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error finalizing kelompok:', error);
        throw error;
    }
}

/**
 * Cari mahasiswa berdasarkan ID atau nama
 * @param {string} query - ID atau nama mahasiswa
 */
async function searchMahasiswa(query) {
    try {
        const response = await fetch(`${API_BASE_URL}/mahasiswa/search?q=${encodeURIComponent(query)}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error searching mahasiswa:', error);
        throw error;
    }
}

/**
 * Get detail tugas besar
 * @param {number} idTugas - ID tugas
 */
async function getTugasBesar(idTugas) {
    try {
        const response = await fetch(`${API_BASE_URL}/tugas/${idTugas}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error getting tugas:', error);
        throw error;
    }
}

// ===============================
// VIEW CONTROLLER
// ===============================
class ViewManager {
    constructor() {
        this.views = {
            default: document.getElementById('defaultView'),
            daftarKelompok: document.getElementById('daftarKelompokView'),
            formKelompok: document.getElementById('formKelompokView')
        };
        
        this.currentView = 'default';
    }

    showView(viewName) {
        // Sembunyikan semua views
        Object.values(this.views).forEach(view => {
            if (view) view.style.display = 'none';
        });

        // Tampilkan view yang dipilih
        if (this.views[viewName]) {
            this.views[viewName].style.display = 'block';
            this.currentView = viewName;
        }
    }

    getCurrentView() {
        return this.currentView;
    }
}

// ===============================
// KELOMPOK CONTROLLER
// ===============================
class KelompokController {
    constructor(viewManager, idTugas) {
        this.viewManager = viewManager;
        this.idTugas = idTugas;
        this.kelompokList = [];
        this.tugasData = null;
        this.init();
    }

    async init() {
        this.setupEventListeners();
        await this.loadTugasData();
        await this.loadKelompok();
    }

    setupEventListeners() {
        // Button: Daftar Kelompok (dari default view)
        const daftarKelompokBtn = document.getElementById('daftarKelompokBtn');
        if (daftarKelompokBtn) {
            daftarKelompokBtn.addEventListener('click', () => {
                this.showDaftarKelompok();
            });
        }

        // Button: Kembali ke default view
        const backToDefaultBtn = document.getElementById('backToDefaultBtn');
        if (backToDefaultBtn) {
            backToDefaultBtn.addEventListener('click', () => {
                this.viewManager.showView('default');
            });
        }

        // Button: Buat Kelompok (dari daftar kelompok view)
        const buatKelompokBtn = document.getElementById('buatKelompokBtn');
        if (buatKelompokBtn) {
            buatKelompokBtn.addEventListener('click', () => {
                this.showFormKelompok();
            });
        }

        // Button: Kembali dari form
        const kembaliFromFormBtn = document.getElementById('kembaliFromFormBtn');
        if (kembaliFromFormBtn) {
            kembaliFromFormBtn.addEventListener('click', () => {
                this.showDaftarKelompok();
            });
        }

        // Button: Finalisasi Kelompok
        const finalisasiBtn = document.getElementById('finalisasiKelompokBtn');
        if (finalisasiBtn) {
            finalisasiBtn.addEventListener('click', () => {
                this.finalisasiKelompok();
            });
        }

        // Form Submit
        const formKelompok = document.getElementById('formKelompok');
        if (formKelompok) {
            formKelompok.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleFormSubmit();
            });
        }

        // Search Button
        const searchBtn = document.querySelector('.search-btn');
        if (searchBtn) {
            searchBtn.addEventListener('click', () => {
                this.handleSearchMahasiswa();
            });
        }
    }

    async loadTugasData() {
        try {
            this.tugasData = await getTugasBesar(this.idTugas);
            console.log('Tugas data loaded:', this.tugasData);
        } catch (error) {
            console.error('Error loading tugas data:', error);
            alert('Gagal memuat data tugas');
        }
    }

    async loadKelompok() {
        try {
            this.kelompokList = await loadKelompokFromDatabase(this.idTugas);
            console.log('Kelompok loaded:', this.kelompokList);
        } catch (error) {
            console.error('Error loading kelompok:', error);
            alert('Gagal memuat data kelompok. Silakan refresh halaman.');
        }
    }

    showDaftarKelompok() {
        this.viewManager.showView('daftarKelompok');
        this.renderKelompokTable();
    }

    showFormKelompok() {
        this.viewManager.showView('formKelompok');
        
        // Reset form
        const form = document.getElementById('formKelompok');
        if (form) form.reset();

        // Set default value dari tugas data
        if (this.tugasData) {
            const maxAnggotaInput = document.getElementById('maxAnggota');
            const minAnggotaInput = document.getElementById('minAnggota');
            
            if (maxAnggotaInput) maxAnggotaInput.value = this.tugasData.max_anggota || 5;
            if (minAnggotaInput) minAnggotaInput.value = this.tugasData.min_anggota || 3;
        }
    }

    renderKelompokTable() {
        const container = document.getElementById('kelompokTableContainer');
        const counterElement = document.getElementById('jumlahKelompok');

        if (!container) return;

        // Update counter
        if (counterElement) {
            counterElement.textContent = this.kelompokList.length;
        }

        // Jika tidak ada kelompok
        if (this.kelompokList.length === 0) {
            container.innerHTML = '<div class="empty-state">Belum ada kelompok yang dibuat</div>';
            return;
        }

        // Buat table
        let tableHTML = `
            <table class="kelompok-table">
                <thead>
                    <tr>
                        <th>No</th>
                        <th>Kelompok</th>
                        <th>Ketua</th>
                        <th>Jumlah Anggota</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
        `;

        this.kelompokList.forEach((kelompok, index) => {
            const jumlahAnggota = kelompok.jumlah_anggota || kelompok.anggota?.length || 1;
            const ketuaNama = kelompok.nama_ketua || kelompok.ketua?.nama || 'N/A';
            
            tableHTML += `
                <tr>
                    <td>${index + 1}.</td>
                    <td>${kelompok.nama_kelompok || 'Kelompok ' + (index + 1)}</td>
                    <td>${ketuaNama}</td>
                    <td>${jumlahAnggota}/${kelompok.max_anggota || this.tugasData?.max_anggota || 'N/A'}</td>
                    <td>
                        <button class="delete-btn" data-id="${kelompok.id_kelompok}">🗑️</button>
                    </td>
                </tr>
            `;
        });

        tableHTML += `
                </tbody>
            </table>
        `;

        container.innerHTML = tableHTML;

        // Attach delete event listeners
        this.attachDeleteListeners();
    }

    attachDeleteListeners() {
        const deleteButtons = document.querySelectorAll('.delete-btn');
        deleteButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const id = parseInt(e.currentTarget.getAttribute('data-id'));
                this.deleteKelompok(id);
            });
        });
    }

    async handleSearchMahasiswa() {
        const namaKetuaInput = document.getElementById('namaKetua');
        if (!namaKetuaInput) return;

        const query = namaKetuaInput.value.trim();
        if (!query) {
            alert('Masukkan ID atau nama mahasiswa untuk mencari');
            return;
        }

        try {
            const results = await searchMahasiswa(query);
            
            if (results && results.length > 0) {
                // Ambil hasil pertama
                const mahasiswa = results[0];
                namaKetuaInput.value = `${mahasiswa.nama} - ${mahasiswa.id_user}`;
                
                // Simpan ID user untuk digunakan saat submit
                namaKetuaInput.dataset.userId = mahasiswa.id_user;
            } else {
                alert('Mahasiswa tidak ditemukan');
            }
        } catch (error) {
            console.error('Error searching mahasiswa:', error);
            alert('Gagal mencari mahasiswa. Silakan coba lagi.');
        }
    }

    async handleFormSubmit() {
        const maxAnggota = parseInt(document.getElementById('maxAnggota').value);
        const minAnggota = parseInt(document.getElementById('minAnggota').value);
        const namaKetuaInput = document.getElementById('namaKetua');
        const namaKetua = namaKetuaInput.value.trim();

        // Validasi
        if (minAnggota > maxAnggota) {
            alert('Jumlah minimal anggota tidak boleh lebih besar dari maksimal!');
            return;
        }

        if (!namaKetua) {
            alert('Nama ketua kelompok harus diisi!');
            return;
        }

        // Extract ID user dari input (jika sudah di-search)
        let idUserKetua = namaKetuaInput.dataset.userId;
        
        // Jika belum di-search, coba extract dari format "Nama - ID"
        if (!idUserKetua && namaKetua.includes('-')) {
            const parts = namaKetua.split('-');
            idUserKetua = parts[parts.length - 1].trim();
        }

        if (!idUserKetua) {
            alert('Silakan gunakan tombol search untuk mencari mahasiswa');
            return;
        }

        // Buat kelompok baru
        const kelompokBaru = {
            id_tugas: this.idTugas,
            nama_kelompok: `Kelompok ${this.kelompokList.length + 1}`,
            id_user_ketua: idUserKetua,
            max_anggota: maxAnggota,
            min_anggota: minAnggota
        };

        try {
            const result = await saveKelompokToDatabase(kelompokBaru);
            
            // Reload kelompok list
            await this.loadKelompok();
            
            // Kembali ke daftar kelompok
            this.showDaftarKelompok();
            
            // Tampilkan notifikasi sukses
            alert('Kelompok berhasil dibuat!');
        } catch (error) {
            console.error('Error saving kelompok:', error);
            alert('Gagal membuat kelompok. Silakan coba lagi.');
        }
    }

    async deleteKelompok(idKelompok) {
        const confirm = window.confirm('Apakah Anda yakin ingin menghapus kelompok ini?');
        
        if (!confirm) return;

        try {
            await deleteKelompokFromDatabase(idKelompok);
            
            // Reload kelompok list
            await this.loadKelompok();
            
            // Re-render table
            this.renderKelompokTable();
            
            alert('Kelompok berhasil dihapus!');
        } catch (error) {
            console.error('Error deleting kelompok:', error);
            alert('Gagal menghapus kelompok. Silakan coba lagi.');
        }
    }

    async finalisasiKelompok() {
        if (this.kelompokList.length === 0) {
            alert('Tidak ada kelompok yang dapat difinalisasi!');
            return;
        }

        const confirm = window.confirm(
            `Anda akan memfinalisasi ${this.kelompokList.length} kelompok. ` +
            'Setelah difinalisasi, kelompok tidak dapat diubah lagi. Lanjutkan?'
        );

        if (!confirm) return;

        try {
            await finalisasiKelompokToDatabase(this.idTugas);
            
            alert('Kelompok berhasil difinalisasi!');
            
            // Reload data
            await this.loadKelompok();
            
            // Kembali ke default view
            this.viewManager.showView('default');
        } catch (error) {
            console.error('Error finalizing kelompok:', error);
            alert('Gagal memfinalisasi kelompok. Silakan coba lagi.');
        }
    }
}

// ===============================
// UTILITY FUNCTIONS
// ===============================

/**
 * Get ID tugas dari URL parameter
 */
function getTugasIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return parseInt(urlParams.get('id_tugas')) || TUGAS_ID;
}

// ===============================
// INITIALIZATION
// ===============================
document.addEventListener('DOMContentLoaded', () => {
    const idTugas = getTugasIdFromUrl();
    const viewManager = new ViewManager();
    const kelompokController = new KelompokController(viewManager, idTugas);
    
    console.log('Kelompok Manager initialized with database integration');
    console.log('ID Tugas:', idTugas);
});