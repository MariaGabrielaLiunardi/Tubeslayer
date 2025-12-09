let kelompokData = [];
let kelompokIdCounter = 1;

// TODO: Ketika database sudah tersedia, hapus fungsi ini dan ganti dengan API calls
function loadKelompokFromDatabase() {
    // HARDCODED DATA - Hapus ini ketika database ready
    // Contoh: return fetch('/api/kelompok').then(res => res.json());
    return Promise.resolve(kelompokData);
}

function saveKelompokToDatabase(kelompok) {
    // HARDCODED - Hapus ini ketika database ready
    // Contoh: return fetch('/api/kelompok', { method: 'POST', body: JSON.stringify(kelompok) });
    kelompokData.push(kelompok);
    return Promise.resolve(kelompok);
}

function deleteKelompokFromDatabase(id) {
    // HARDCODED - Hapus ini ketika database ready
    // Contoh: return fetch(`/api/kelompok/${id}`, { method: 'DELETE' });
    kelompokData = kelompokData.filter(k => k.id !== id);
    return Promise.resolve();
}

function finalisasiKelompokToDatabase() {
    // HARDCODED - Hapus ini ketika database ready
    // Contoh: return fetch('/api/kelompok/finalisasi', { method: 'POST' });
    console.log('Kelompok difinalisasi:', kelompokData);
    return Promise.resolve();
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
    constructor(viewManager) {
        this.viewManager = viewManager;
        this.kelompokList = [];
        this.init();
    }

    async init() {
        this.setupEventListeners();
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
    }

    async loadKelompok() {
        try {
            // TODO: Replace with actual database call
            this.kelompokList = await loadKelompokFromDatabase();
        } catch (error) {
            console.error('Error loading kelompok:', error);
            alert('Gagal memuat data kelompok');
        }
    }

    showDaftarKelompok() {
        this.viewManager.showView('daftarKelompok');
        this.renderKelompokTable();
    }

    showFormKelompok() {
        this.viewManager.showView('formKelompok');
        // Reset form
        document.getElementById('formKelompok').reset();
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
            tableHTML += `
                <tr>
                    <td>${index + 1}.</td>
                    <td>Kelompok ${index + 1}</td>
                    <td>${kelompok.namaKetua}</td>
                    <td>${kelompok.jumlahAnggota}/${kelompok.maxAnggota}</td>
                    <td>
                        <button class="delete-btn" data-id="${kelompok.id}">🗑️</button>
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

    async handleFormSubmit() {
        const maxAnggota = parseInt(document.getElementById('maxAnggota').value);
        const minAnggota = parseInt(document.getElementById('minAnggota').value);
        const namaKetua = document.getElementById('namaKetua').value.trim();

        // Validasi
        if (minAnggota > maxAnggota) {
            alert('Jumlah minimal anggota tidak boleh lebih besar dari maksimal!');
            return;
        }

        if (!namaKetua) {
            alert('Nama ketua kelompok harus diisi!');
            return;
        }

        // Buat kelompok baru
        const kelompokBaru = {
            id: kelompokIdCounter++, // TODO: Ini akan di-handle oleh database
            maxAnggota: maxAnggota,
            minAnggota: minAnggota,
            namaKetua: namaKetua,
            jumlahAnggota: 1, // Ketua sudah masuk
            createdAt: new Date().toISOString()
        };

        try {
            // TODO: Replace with actual database call
            await saveKelompokToDatabase(kelompokBaru);
            this.kelompokList.push(kelompokBaru);
            
            // Kembali ke daftar kelompok
            this.showDaftarKelompok();
            
            // Tampilkan notifikasi sukses
            alert('Kelompok berhasil dibuat!');
        } catch (error) {
            console.error('Error saving kelompok:', error);
            alert('Gagal membuat kelompok. Silakan coba lagi.');
        }
    }

    async deleteKelompok(id) {
        const confirm = window.confirm('Apakah Anda yakin ingin menghapus kelompok ini?');
        
        if (!confirm) return;

        try {
            // TODO: Replace with actual database call
            await deleteKelompokFromDatabase(id);
            
            // Hapus dari array lokal
            this.kelompokList = this.kelompokList.filter(k => k.id !== id);
            
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
            // TODO: Replace with actual database call
            await finalisasiKelompokToDatabase();
            
            alert('Kelompok berhasil difinalisasi!');
            
            // Redirect atau update UI sesuai kebutuhan
            // Misalnya kembali ke default view
            this.viewManager.showView('default');
        } catch (error) {
            console.error('Error finalizing kelompok:', error);
            alert('Gagal memfinalisasi kelompok. Silakan coba lagi.');
        }
    }
}

// ===============================
// INITIALIZATION
// ===============================
document.addEventListener('DOMContentLoaded', () => {
    const viewManager = new ViewManager();
    const kelompokController = new KelompokController(viewManager);
    
    console.log('Kelompok Manager initialized');
    console.log('TODO: Integrate with database by replacing hardcoded functions');
});

// ===============================
// DATABASE INTEGRATION GUIDE
// ===============================
/*
PANDUAN INTEGRASI DATABASE:

1. HAPUS semua fungsi dengan prefix "HARDCODED" di bagian DATA LAYER:
   - loadKelompokFromDatabase()
   - saveKelompokToDatabase()
   - deleteKelompokFromDatabase()
   - finalisasiKelompokToDatabase()

2. GANTI dengan API calls yang sesuai, contoh menggunakan Fetch API:

   async function loadKelompokFromDatabase() {
       const response = await fetch('/api/kelompok');
       if (!response.ok) throw new Error('Failed to load kelompok');
       return await response.json();
   }

   async function saveKelompokToDatabase(kelompok) {
       const response = await fetch('/api/kelompok', {
           method: 'POST',
           headers: { 'Content-Type': 'application/json' },
           body: JSON.stringify(kelompok)
       });
       if (!response.ok) throw new Error('Failed to save kelompok');
       return await response.json();
   }

   async function deleteKelompokFromDatabase(id) {
       const response = await fetch(`/api/kelompok/${id}`, {
           method: 'DELETE'
       });
       if (!response.ok) throw new Error('Failed to delete kelompok');
       return await response.json();
   }

   async function finalisasiKelompokToDatabase() {
       const response = await fetch('/api/kelompok/finalisasi', {
           method: 'POST'
       });
       if (!response.ok) throw new Error('Failed to finalize kelompok');
       return await response.json();
   }

3. HAPUS variabel global:
   - let kelompokData = [];
   - let kelompokIdCounter = 1;

4. SESUAIKAN struktur response dari server dengan struktur data yang digunakan

5. TAMBAHKAN error handling dan loading states sesuai kebutuhan

6. OPTIONAL: Tambahkan token authentication jika diperlukan:
   headers: {
       'Content-Type': 'application/json',
       'Authorization': `Bearer ${yourToken}`
   }
*/