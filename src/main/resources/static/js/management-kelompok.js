const API_BASE_URL = '/api';

const TUGAS_DATA = window.TUGAS_DATA || {
    idTugas: 1,
    judulTugas: 'Tugas Besar',
    deskripsi: 'Deskripsi',
    maxAnggota: 5,
    minAnggota: 3,
    modeKel: 'Kelompok',
    deadline: 'Senin, 27 November 2025'
};

let currentMembers = [];
let currentKelompokId = null;
let currentKelompokName = '';
let maxAnggota = 5;


async function loadKelompokFromDatabase(idTugas) {
    try {
        const response = await fetch(`${API_BASE_URL}/kelompok/tugas/${idTugas}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('Error loading kelompok:', error);
        throw error;
    }
}

async function saveKelompokToDatabase(kelompok) {
    try {
        const response = await fetch(`${API_BASE_URL}/kelompok`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(kelompok)
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('Error saving kelompok:', error);
        throw error;
    }
}

async function deleteKelompokFromDatabase(idKelompok) {
    try {
        const response = await fetch(`${API_BASE_URL}/kelompok/${idKelompok}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('Error deleting kelompok:', error);
        throw error;
    }
}

async function finalisasiKelompokToDatabase(idTugas) {
    try {
        const response = await fetch(`${API_BASE_URL}/kelompok/finalisasi/${idTugas}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('Error finalizing kelompok:', error);
        throw error;
    }
}

async function searchMahasiswa(query) {
    try {
        const response = await fetch(`${API_BASE_URL}/mahasiswa/search?q=${encodeURIComponent(query)}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('Error searching mahasiswa:', error);
        throw error;
    }
}

async function getTugasBesar(idTugas) {
    try {
        const response = await fetch(`${API_BASE_URL}/tugas/${idTugas}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('Error getting tugas:', error);
        throw error;
    }
}

async function loadAnggotaKelompok(idKelompok) {
    try {
        const response = await fetch(`${API_BASE_URL}/kelompok/${idKelompok}/anggota`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('Error loading anggota:', error);
        throw error;
    }
}

async function tambahAnggotaToDatabase(idKelompok, idAnggota) {
    try {
        const response = await fetch(`${API_BASE_URL}/kelompok/tambah-anggota`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idKelompok, idAnggota })
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('Error adding member:', error);
        throw error;
    }
}

async function hapusAnggotaFromDatabase(idKelompok, idAnggota) {
    try {
        const response = await fetch(`${API_BASE_URL}/kelompok/hapus-anggota`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idKelompok, idAnggota })
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('Error removing member:', error);
        throw error;
    }
}

class ViewManager {
    constructor() {
        this.views = {
            default: document.getElementById('defaultView'),
            daftarKelompok: document.getElementById('daftarKelompokView'),
            formKelompok: document.getElementById('formKelompokView'),
            detailAnggota: document.getElementById('detailAnggotaView'),
            pilihAnggota: document.getElementById('pilihAnggotaView')
        };
        this.currentView = 'default';
    }

    showView(viewName) {
        Object.values(this.views).forEach(view => {
            if (view) view.style.display = 'none';
        });
        if (this.views[viewName]) {
            this.views[viewName].style.display = 'block';
            this.currentView = viewName;
        }
    }

    getCurrentView() {
        return this.currentView;
    }
}

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
        this.setupAnggotaEventListeners();
        await this.loadTugasData();
        await this.loadKelompok();
    }

    setupEventListeners() {
        const daftarKelompokBtn = document.getElementById('daftarKelompokBtn');
        if (daftarKelompokBtn) {
            daftarKelompokBtn.addEventListener('click', () => this.showDaftarKelompok());
        }

        const backToDefaultBtn = document.getElementById('backToDefaultBtn');
        if (backToDefaultBtn) {
            backToDefaultBtn.addEventListener('click', () => this.viewManager.showView('default'));
        }

        const buatKelompokBtn = document.getElementById('buatKelompokBtn');
        if (buatKelompokBtn) {
            buatKelompokBtn.addEventListener('click', () => this.showFormKelompok());
        }

        const kembaliFromFormBtn = document.getElementById('kembaliFromFormBtn');
        if (kembaliFromFormBtn) {
            kembaliFromFormBtn.addEventListener('click', () => this.showDaftarKelompok());
        }

        const finalisasiBtn = document.getElementById('finalisasiKelompokBtn');
        if (finalisasiBtn) {
            finalisasiBtn.addEventListener('click', () => this.finalisasiKelompok());
        }

        const formKelompok = document.getElementById('formKelompok');
        if (formKelompok) {
            formKelompok.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleFormSubmit();
            });
        }

        const searchBtn = document.querySelector('.search-btn');
        if (searchBtn) {
            searchBtn.addEventListener('click', () => this.handleSearchMahasiswa());
        }
    }

    setupAnggotaEventListeners() {
        const btnKembaliAnggota = document.getElementById('btn-kembali-anggota-dosen');
        if (btnKembaliAnggota) {
            btnKembaliAnggota.addEventListener('click', () => {
                this.viewManager.showView('daftarKelompok');
                currentKelompokId = null;
            });
        }

        const btnTambahAnggota = document.getElementById('btn-tambah-anggota-dosen');
        if (btnTambahAnggota) {
            btnTambahAnggota.addEventListener('click', () => {
                this.viewManager.showView('pilihAnggota');
            });
        }

        const btnSelesaiAnggota = document.getElementById('btn-selesai-anggota-dosen');
        if (btnSelesaiAnggota) {
            btnSelesaiAnggota.addEventListener('click', () => {
                this.viewManager.showView('daftarKelompok');
                currentKelompokId = null;
            });
        }

        const btnKembaliPilih = document.getElementById('btn-kembali-pilih-dosen');
        if (btnKembaliPilih) {
            btnKembaliPilih.addEventListener('click', () => {
                this.viewManager.showView('detailAnggota');
                this.clearSearchResults();
            });
        }

        const btnKonfirmasi = document.getElementById('btn-konfirmasi-dosen');
        if (btnKonfirmasi) {
            btnKonfirmasi.addEventListener('click', () => {
                this.viewManager.showView('detailAnggota');
                this.clearSearchResults();
            });
        }

        this.setupSearchFunctionality();
    }

    async loadTugasData() {
        try {
            this.tugasData = await getTugasBesar(this.idTugas);
            maxAnggota = this.tugasData.max_anggota || 5;
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
        const form = document.getElementById('formKelompok');
        if (form) form.reset();

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

        if (counterElement) {
            counterElement.textContent = this.kelompokList.length;
        }

        if (this.kelompokList.length === 0) {
            container.innerHTML = '<div class="empty-state">Belum ada kelompok yang dibuat</div>';
            return;
        }

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
                <tr data-kelompok-id="${kelompok.id_kelompok}" data-kelompok-name="${kelompok.nama_kelompok || 'Kelompok ' + (index + 1)}">
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

        tableHTML += `</tbody></table>`;
        container.innerHTML = tableHTML;

        this.attachDeleteListeners();
        this.attachRowClickListeners();
    }

    attachDeleteListeners() {
        const deleteButtons = document.querySelectorAll('.delete-btn');
        deleteButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation(); // Prevent row click
                const id = parseInt(e.currentTarget.getAttribute('data-id'));
                this.deleteKelompok(id);
            });
        });
    }

    attachRowClickListeners() {
        const rows = document.querySelectorAll('.kelompok-table tbody tr');
        rows.forEach(row => {
            row.style.cursor = 'pointer';
            row.addEventListener('click', (e) => {
                if (e.target.closest('.delete-btn')) return;
                
                const kelompokId = parseInt(row.getAttribute('data-kelompok-id'));
                const kelompokName = row.getAttribute('data-kelompok-name');
                
                console.log('Row clicked:', { kelompokId, kelompokName });
                
                if (kelompokId) {
                    this.showDetailAnggotaView(kelompokId, kelompokName);
                }
            });
        });
    }

    async showDetailAnggotaView(kelompokId, kelompokName) {
        currentKelompokId = kelompokId;
        currentKelompokName = kelompokName;

        const breadcrumb = document.getElementById('breadcrumbKelompokName');
        const subtitle = document.getElementById('kelompokNameSubtitle');
        
        if (breadcrumb) breadcrumb.textContent = kelompokName;
        if (subtitle) subtitle.textContent = kelompokName;

        this.viewManager.showView('detailAnggota');
        await this.loadAndDisplayAnggota(kelompokId);
    }

    async loadAndDisplayAnggota(kelompokId) {
        const memberList = document.getElementById('member-list-dosen');
        if (!memberList) return;

        memberList.innerHTML = '<p class="no-members">Memuat data anggota...</p>';

        try {
            currentMembers = await loadAnggotaKelompok(kelompokId);
            this.updateMemberDisplay();
            this.updateMemberCounter();
        } catch (error) {
            console.error('Error loading anggota:', error);
            memberList.innerHTML = `
                <div class="error-message">
                    <i class='bx bx-error'></i>
                    <p>Gagal memuat data anggota kelompok</p>
                    <small>${error.message}</small>
                </div>
            `;
        }
    }

    updateMemberDisplay() {
        const memberList = document.getElementById('member-list-dosen');
        if (!memberList) return;

        memberList.innerHTML = '';

        if (currentMembers.length === 0) {
            memberList.innerHTML = '<p class="no-members">Belum ada anggota kelompok</p>';
            return;
        }

        currentMembers.forEach(member => {
            const memberItem = document.createElement('div');
            memberItem.className = 'member-item';
            
            const isLeader = member.role === 'leader';
            const memberName = member.nama || 'Unknown';
            const memberId = member.idUser || '';
            
            memberItem.innerHTML = `
                <div class="member-info">
                    <span class="member-name">${memberName}</span>
                    ${isLeader ? '<span class="member-role-badge">Ketua</span>' : ''}
                </div>
                ${!isLeader ? `
                    <button class="btn-remove-member" data-id="${memberId}" data-nama="${memberName}">
                        <i class='bx bx-minus'></i>
                    </button>
                ` : ''}
            `;

            if (!isLeader) {
                const removeBtn = memberItem.querySelector('.btn-remove-member');
                if (removeBtn) {
                    removeBtn.addEventListener('click', () => {
                        this.confirmRemoveMember(memberId, memberName);
                    });
                }
            }

            memberList.appendChild(memberItem);
        });
    }

    updateMemberCounter() {
        const counters = [
            document.getElementById('member-counter-dosen'),
            document.getElementById('member-counter-pilih-dosen')
        ];

        counters.forEach(counter => {
            if (counter) {
                counter.textContent = `${currentMembers.length}/${maxAnggota} Anggota`;
            }
        });
    }

    setupSearchFunctionality() {
        const searchInput = document.getElementById('search-mahasiswa-dosen');
        const searchButton = document.getElementById('btn-search-dosen');

        if (!searchInput || !searchButton) return;

        searchButton.addEventListener('click', (e) => {
            e.preventDefault();
            this.performSearch();
        });

        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                this.performSearch();
            }
        });

        searchInput.addEventListener('input', () => {
            if (searchInput.value.trim() === '') {
                this.clearSearchResults();
            }
        });
    }

    async performSearch() {
        const searchInput = document.getElementById('search-mahasiswa-dosen');
        const searchResults = document.getElementById('search-results-dosen');
        const keyword = searchInput.value.trim();

        if (!keyword) {
            this.showSearchError('Masukkan nama mahasiswa untuk mencari');
            return;
        }

        searchResults.innerHTML = '<div class="search-loading">Mencari...</div>';
        searchResults.classList.add('active');

        try {
            const data = await searchMahasiswa(keyword);
            this.displaySearchResults(data);
        } catch (error) {
            console.error('Error:', error);
            this.showSearchError(error.message || 'Gagal mencari mahasiswa');
        }
    }

    displaySearchResults(mahasiswaList) {
        const searchResults = document.getElementById('search-results-dosen');
        searchResults.innerHTML = '';

        if (mahasiswaList.length === 0) {
            searchResults.innerHTML = '<div class="search-no-result">Tidak ada mahasiswa yang ditemukan</div>';
            searchResults.classList.add('active');
            return;
        }

        const existingIds = currentMembers.map(m => m.idUser);
        const availableMahasiswa = mahasiswaList.filter(mhs => !existingIds.includes(mhs.id_user));

        if (availableMahasiswa.length === 0) {
            searchResults.innerHTML = '<div class="search-no-result">Semua mahasiswa sudah tergabung dalam kelompok</div>';
            searchResults.classList.add('active');
            return;
        }

        availableMahasiswa.forEach(mhs => {
            const resultItem = document.createElement('div');
            resultItem.className = 'search-result-item';
            resultItem.innerHTML = `
                <div class="result-info">
                    <span class="result-name">${mhs.nama}</span>
                    <span class="result-npm">${mhs.id_user}</span>
                </div>
                <button class="btn-add-member" data-id="${mhs.id_user}" data-nama="${mhs.nama}">
                    <i class='bx bx-plus'></i>
                </button>
            `;

            const addButton = resultItem.querySelector('.btn-add-member');
            addButton.addEventListener('click', () => this.addMemberToGroup(mhs));

            searchResults.appendChild(resultItem);
        });

        searchResults.classList.add('active');
    }

    async addMemberToGroup(mahasiswa) {
        if (!currentKelompokId) {
            this.showError('ID Kelompok tidak ditemukan');
            return;
        }
        if (currentMembers.length >= maxAnggota) {
            this.showError('Kelompok sudah penuh');
            return;
        }

        try {
            await tambahAnggotaToDatabase(currentKelompokId, mahasiswa.id_user);
            document.getElementById('search-mahasiswa-dosen').value = '';
            this.clearSearchResults();
            await this.loadAndDisplayAnggota(currentKelompokId);
            this.showSuccess(`${mahasiswa.nama} berhasil ditambahkan`);
        } catch (error) {
            console.error('Error adding member:', error);
            this.showError(error.message || 'Gagal menambahkan anggota');
        }
    }

    confirmRemoveMember(idUser, nama) {
        if (confirm(`Apakah Anda yakin ingin menghapus ${nama} dari kelompok?`)) {
            this.removeMemberFromGroup(idUser, nama);
        }
    }

    async removeMemberFromGroup(idUser, nama) {
        if (!currentKelompokId) {
            this.showError('ID Kelompok tidak ditemukan');
            return;
        }

        try {
            await hapusAnggotaFromDatabase(currentKelompokId, idUser);
            await this.loadAndDisplayAnggota(currentKelompokId);
            this.showSuccess(`${nama} berhasil dihapus dari kelompok`);
        } catch (error) {
            console.error('Error removing member:', error);
            this.showError(error.message || 'Gagal menghapus anggota');
        }
    }

    clearSearchResults() {
        const searchResults = document.getElementById('search-results-dosen');
        const searchInput = document.getElementById('search-mahasiswa-dosen');
        if (searchResults) {
            searchResults.innerHTML = '';
            searchResults.classList.remove('active');
        }
        if (searchInput) searchInput.value = '';
    }

    showSuccess(message) {
        this.showToast(message, 'success');
    }

    showError(message) {
        this.showToast(message, 'error');
    }

    showSearchError(message) {
        const searchResults = document.getElementById('search-results-dosen');
        searchResults.innerHTML = `
            <div class="search-error">
                <i class='bx bx-error-circle'></i>
                <span>${message}</span>
            </div>
        `;
        searchResults.classList.add('active');
        setTimeout(() => searchResults.classList.remove('active'), 3000);
    }

    showToast(message, type) {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
            <i class='bx ${type === 'success' ? 'bx-check-circle' : 'bx-error-circle'}'></i>
            <span>${message}</span>
        `;
        
        document.body.appendChild(toast);
        setTimeout(() => toast.classList.add('show'), 100);
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 3000);
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
                const mahasiswa = results[0];
                namaKetuaInput.value = `${mahasiswa.nama} - ${mahasiswa.id_user}`;
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

        if (minAnggota > maxAnggota) {
            alert('Jumlah minimal anggota tidak boleh lebih besar dari maksimal!');
            return;
        }

        if (!namaKetua) {
            alert('Nama ketua kelompok harus diisi!');
            return;
        }

        let idUserKetua = namaKetuaInput.dataset.userId;
        if (!idUserKetua && namaKetua.includes('-')) {
            const parts = namaKetua.split('-');
            idUserKetua = parts[parts.length - 1].trim();
        }

        if (!idUserKetua) {
            alert('Silakan gunakan tombol search untuk mencari mahasiswa');
            return;
        }

        const kelompokBaru = {
            id_tugas: this.idTugas,
            nama_kelompok: `Kelompok ${this.kelompokList.length + 1}`,
            id_user_ketua: idUserKetua,
            max_anggota: maxAnggota,
            min_anggota: minAnggota
        };

        try {
            await saveKelompokToDatabase(kelompokBaru);
            await this.loadKelompok();
            this.showDaftarKelompok();
            alert('Kelompok berhasil dibuat!');
        } catch (error) {
            console.error('Error saving kelompok:', error);
            alert('Gagal membuat kelompok. Silakan coba lagi.');
        }
    }

    async deleteKelompok(idKelompok) {
        if (!confirm('Apakah Anda yakin ingin menghapus kelompok ini?')) return;

        try {
            await deleteKelompokFromDatabase(idKelompok);
            await this.loadKelompok();
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

        if (!confirm(`Anda akan memfinalisasi ${this.kelompokList.length} kelompok. Setelah difinalisasi, kelompok tidak dapat diubah lagi. Lanjutkan?`)) return;

        try {
            await finalisasiKelompokToDatabase(this.idTugas);
            alert('Kelompok berhasil difinalisasi!');
            await this.loadKelompok();
            this.viewManager.showView('default');
        } catch (error) {
            console.error('Error finalizing kelompok:', error);
            alert('Gagal memfinalisasi kelompok. Silakan coba lagi.');
        }
    }
}

document.addEventListener("DOMContentLoaded", () => {
  const idTugas = TUGAS_DATA.idTugas;
  const viewManager = new ViewManager();
  const kelompokController = new KelompokController(viewManager, idTugas);

  if (typeof setupKelompokTableClickHandler === "function") {
    setupKelompokTableClickHandler();
  }
  if (typeof setupAnggotaViewNavigation === "function") {
    setupAnggotaViewNavigation();
  }
  setupLogoutButton();

  console.log("Kelompok Manager initialized with database integration");
  console.log("Tugas Data:", TUGAS_DATA);
});
