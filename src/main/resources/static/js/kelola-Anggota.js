// State management
let currentMembers = [];
let maxAnggota = 5;
let minAnggota = 1;

// Ambil idTugas dari hidden input atau URL
function getIdTugas() {
    // Coba ambil dari hidden input dulu
    const hiddenInput = document.getElementById('idTugas');
    if (hiddenInput && hiddenInput.value) {
        return hiddenInput.value;
    }
    
    // Fallback ke URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('idTugas');
}

// Get max anggota dari hidden input
function getMaxAnggota() {
    const hiddenInput = document.getElementById('maxAnggota');
    if (hiddenInput && hiddenInput.value) {
        return parseInt(hiddenInput.value);
    }
    return 5; // default
}

// Check apakah user adalah leader
function checkIsLeader() {
    const hiddenInput = document.getElementById('isLeader');
    if (hiddenInput && hiddenInput.value) {
        return hiddenInput.value === 'true';
    }
    return false;
}

// Initialize saat DOM ready
document.addEventListener('DOMContentLoaded', function() {
    const idTugas = getIdTugas();
    
    if (!idTugas) {
        console.error('ID Tugas tidak ditemukan');
        return;
    }

    // Set maxAnggota dari hidden input
    maxAnggota = getMaxAnggota();
    
    // Check leader status
    const isLeader = checkIsLeader();
    console.log('Is Leader:', isLeader);
    
    // Disable buttons jika bukan leader
    if (!isLeader) {
        const btnTambah = document.getElementById('btn-tambah-anggota');
        const btnKelola = document.getElementById('btn-kelola-anggota');
        
        if (btnTambah) btnTambah.disabled = true;
        if (btnKelola) btnKelola.disabled = true;
    }

    // Load anggota kelompok saat halaman dimuat
    loadAnggotaKelompok();

    // Setup event listeners
    setupSearchFunctionality();
    setupViewNavigation();
});

// ============= LOAD ANGGOTA KELOMPOK =============
async function loadAnggotaKelompok() {
    const idTugas = getIdTugas();
    
    try {
        const response = await fetch(`/mahasiswa/api/anggota-kelompok?idTugas=${idTugas}`);
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Gagal memuat anggota');
        }

        currentMembers = data;
        updateMemberDisplay();
        updateMemberCounter();

    } catch (error) {
        console.error('Error loading anggota:', error);
        showError('Gagal memuat data anggota kelompok');
    }
}

// Update tampilan list anggota
function updateMemberDisplay() {
    const memberList = document.getElementById('member-list');
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
        const roleLabel = isLeader ? 'Ketua' : 'Anggota';
        
        memberItem.innerHTML = `
            <div class="member-info">
                <span class="member-name">${member.user.nama}</span>
                <span class="member-npm">${member.user.idUser}</span>
                <span class="member-role ${isLeader ? 'role-leader' : 'role-member'}">${roleLabel}</span>
            </div>
            ${!isLeader ? `
                <button class="btn-remove-member" data-id="${member.user.idUser}" data-nama="${member.user.nama}">
                    <i class='bx bx-trash'></i>
                </button>
            ` : ''}
        `;

        // Add event listener untuk tombol hapus
        if (!isLeader) {
            const removeBtn = memberItem.querySelector('.btn-remove-member');
            removeBtn.addEventListener('click', function() {
                confirmRemoveMember(member.user.idUser, member.user.nama);
            });
        }

        memberList.appendChild(memberItem);
    });
}

// Update counter anggota
function updateMemberCounter() {
    const counters = document.querySelectorAll('.member-counter');
    counters.forEach(counter => {
        counter.textContent = `${currentMembers.length}/${maxAnggota} Anggota`;
    });
}

// ============= SEARCH FUNCTIONALITY =============
function setupSearchFunctionality() {
    const searchInput = document.getElementById('search-mahasiswa');
    const searchButton = document.getElementById('btn-search');
    const searchResults = document.getElementById('search-results');

    if (!searchInput || !searchButton || !searchResults) {
        console.error('Element pencarian tidak ditemukan');
        return;
    }

    // Handle click pada tombol search
    searchButton.addEventListener('click', function(e) {
        e.preventDefault();
        performSearch();
    });

    // Handle enter key
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            performSearch();
        }
    });

    // Clear results on input clear
    searchInput.addEventListener('input', function() {
        if (searchInput.value.trim() === '') {
            searchResults.innerHTML = '';
            searchResults.style.display = 'none';
        }
    });

    // Close results when clicking outside
    document.addEventListener('click', function(e) {
        if (!searchInput.contains(e.target) && 
            !searchButton.contains(e.target) && 
            !searchResults.contains(e.target)) {
            searchResults.style.display = 'none';
        }
    });
}

// Perform search
async function performSearch() {
    const searchInput = document.getElementById('search-mahasiswa');
    const searchResults = document.getElementById('search-results');
    const keyword = searchInput.value.trim();
    const idTugas = getIdTugas();

    if (!keyword) {
        showSearchError('Masukkan nama mahasiswa untuk mencari');
        return;
    }

    // Show loading
    searchResults.innerHTML = '<div class="search-loading">Mencari...</div>';
    searchResults.style.display = 'block';

    try {
        const response = await fetch('/mahasiswa/api/search-mahasiswa', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                idTugas: parseInt(idTugas),
                keyword: keyword
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Terjadi kesalahan');
        }

        displaySearchResults(data);

    } catch (error) {
        console.error('Error:', error);
        showSearchError(error.message || 'Gagal mencari mahasiswa');
    }
}

// Display search results
function displaySearchResults(mahasiswaList) {
    const searchResults = document.getElementById('search-results');
    searchResults.innerHTML = '';

    if (mahasiswaList.length === 0) {
        searchResults.innerHTML = '<div class="search-no-result">Tidak ada mahasiswa yang ditemukan</div>';
        searchResults.style.display = 'block';
        return;
    }

    mahasiswaList.forEach(mhs => {
        const resultItem = document.createElement('div');
        resultItem.className = 'search-result-item';
        resultItem.innerHTML = `
            <div class="result-info">
                <span class="result-name">${mhs.nama}</span>
                <span class="result-npm">${mhs.npm}</span>
            </div>
            <button class="btn-add-member" data-id="${mhs.idUser}" data-nama="${mhs.nama}">
                <i class='bx bx-plus'></i>
            </button>
        `;

        const addButton = resultItem.querySelector('.btn-add-member');
        addButton.addEventListener('click', function() {
            addMemberToGroup(mhs);
        });

        searchResults.appendChild(resultItem);
    });

    searchResults.style.display = 'block';
}

// Add member to group
async function addMemberToGroup(mahasiswa) {
    const idTugas = getIdTugas();

    // Validasi jumlah maksimal
    if (currentMembers.length >= maxAnggota) {
        showError('Kelompok sudah penuh');
        return;
    }

    try {
        const response = await fetch('/mahasiswa/api/tambah-anggota', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                idTugas: parseInt(idTugas),
                idAnggota: mahasiswa.idUser
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Gagal menambahkan anggota');
        }

        // Clear search
        document.getElementById('search-mahasiswa').value = '';
        document.getElementById('search-results').style.display = 'none';

        // Reload anggota
        await loadAnggotaKelompok();

        showSuccess(`${mahasiswa.nama} berhasil ditambahkan`);

    } catch (error) {
        console.error('Error adding member:', error);
        showError(error.message || 'Gagal menambahkan anggota');
    }
}

// Confirm remove member
function confirmRemoveMember(idUser, nama) {
    if (confirm(`Apakah Anda yakin ingin menghapus ${nama} dari kelompok?`)) {
        removeMemberFromGroup(idUser, nama);
    }
}

// Remove member from group
async function removeMemberFromGroup(idUser, nama) {
    const idTugas = getIdTugas();

    try {
        const response = await fetch('/mahasiswa/api/hapus-anggota', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                idTugas: parseInt(idTugas),
                idAnggota: idUser
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Gagal menghapus anggota');
        }

        // Reload anggota
        await loadAnggotaKelompok();

        showSuccess(`${nama} berhasil dihapus dari kelompok`);

    } catch (error) {
        console.error('Error removing member:', error);
        showError(error.message || 'Gagal menghapus anggota');
    }
}

// ============= VIEW NAVIGATION =============
function setupViewNavigation() {
    // Button Kelola Anggota - dari view deskripsi ke view anggota
    const btnKelolaAnggota = document.getElementById('btn-kelola-anggota');
    if (btnKelolaAnggota) {
        btnKelolaAnggota.addEventListener('click', function() {
            showView('view-anggota');
            loadAnggotaKelompok();
        });
    }

    // Button Kembali dari view anggota ke view deskripsi
    const btnKembaliAnggota = document.getElementById('btn-kembali-anggota');
    if (btnKembaliAnggota) {
        btnKembaliAnggota.addEventListener('click', function() {
            showView('view-deskripsi');
        });
    }

    // Button Tambah - dari view anggota ke view pilih anggota
    const btnTambahAnggota = document.getElementById('btn-tambah-anggota');
    if (btnTambahAnggota) {
        btnTambahAnggota.addEventListener('click', function() {
            showView('view-pilih-anggota');
        });
    }

    // Button Selesai dari view anggota ke view deskripsi
    const btnSelesaiAnggota = document.getElementById('btn-selesai-anggota');
    if (btnSelesaiAnggota) {
        btnSelesaiAnggota.addEventListener('click', function() {
            showView('view-deskripsi');
        });
    }

    // Button Kembali dari view pilih anggota ke view anggota
    const btnKembaliPilih = document.getElementById('btn-kembali-pilih');
    if (btnKembaliPilih) {
        btnKembaliPilih.addEventListener('click', function() {
            showView('view-anggota');
            // Clear search
            document.getElementById('search-mahasiswa').value = '';
            document.getElementById('search-results').style.display = 'none';
        });
    }

    // Button Konfirmasi dari view pilih anggota ke view anggota
    const btnKonfirmasi = document.getElementById('btn-konfirmasi');
    if (btnKonfirmasi) {
        btnKonfirmasi.addEventListener('click', function() {
            showView('view-anggota');
            // Clear search
            document.getElementById('search-mahasiswa').value = '';
            document.getElementById('search-results').style.display = 'none';
        });
    }
}

// Show specific view
function showView(viewId) {
    // Hide all views
    const allViews = document.querySelectorAll('.view-content');
    allViews.forEach(view => {
        view.classList.remove('active');
    });

    // Show selected view
    const selectedView = document.getElementById(viewId);
    if (selectedView) {
        selectedView.classList.add('active');
    }
}

// ============= NOTIFICATION FUNCTIONS =============
function showSuccess(message) {
    const toast = document.createElement('div');
    toast.className = 'toast toast-success';
    toast.innerHTML = `
        <i class='bx bx-check-circle'></i>
        <span>${message}</span>
    `;
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.classList.add('show');
    }, 100);
    
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function showError(message) {
    const toast = document.createElement('div');
    toast.className = 'toast toast-error';
    toast.innerHTML = `
        <i class='bx bx-error-circle'></i>
        <span>${message}</span>
    `;
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.classList.add('show');
    }, 100);
    
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function showSearchError(message) {
    const searchResults = document.getElementById('search-results');
    searchResults.innerHTML = `
        <div class="search-error">
            <i class='bx bx-error-circle'></i>
            <span>${message}</span>
        </div>
    `;
    searchResults.style.display = 'block';

    setTimeout(() => {
        searchResults.style.display = 'none';
    }, 3000);
}