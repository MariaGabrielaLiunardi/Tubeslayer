let currentMembers = [];
let maxAnggota = 5;
let minAnggota = 1;

function getIdTugas() {
    const hiddenInput = document.getElementById('idTugas');
    if (hiddenInput && hiddenInput.value) {
        return hiddenInput.value;
    }
    
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('idTugas');
}


function getMaxAnggota() {
    const hiddenInput = document.getElementById('maxAnggota');
    if (hiddenInput && hiddenInput.value) {
        return parseInt(hiddenInput.value);
    }
    return 5; 
}


function checkIsLeader() {
    const hiddenInput = document.getElementById('isLeader');
    if (hiddenInput && hiddenInput.value) {
        return hiddenInput.value === 'true';
    }
    return false;
}


function checkCanManage() {
    const hiddenInput = document.getElementById('canManageAnggota');
    if (hiddenInput && hiddenInput.value) {
        return hiddenInput.value === 'true';
    }
    return false;
}


function getModeKelompok() {
    const hiddenInput = document.getElementById('modeKelompok');
    if (hiddenInput && hiddenInput.value) {
        return hiddenInput.value;
    }
    return 'Mahasiswa';
}


document.addEventListener('DOMContentLoaded', function() {
    const idTugas = getIdTugas();
    
    if (!idTugas) {
        console.error('ID Tugas tidak ditemukan');
        return;
    }

 
    maxAnggota = getMaxAnggota();
    

    const isLeader = checkIsLeader();
    const canManage = checkCanManage();
    const modeKelompok = getModeKelompok();
    
    console.log('User Status:', {
        isLeader: isLeader,
        canManage: canManage,
        modeKelompok: modeKelompok
    });
    

    if (!canManage) {
        const btnTambah = document.getElementById('btn-tambah-anggota');
        const btnKelola = document.getElementById('btn-kelola-anggota');
        
        if (btnTambah) {
            btnTambah.disabled = true;
            btnTambah.title = modeKelompok === 'Dosen' 
                ? 'Kelompok diatur oleh Dosen' 
                : 'Hanya ketua kelompok yang dapat mengelola anggota';
        }
        
        if (btnKelola) {
            btnKelola.disabled = true;
            btnKelola.title = modeKelompok === 'Dosen' 
                ? 'Kelompok diatur oleh Dosen' 
                : 'Hanya ketua kelompok yang dapat mengelola anggota';
        }
        

        const warning = document.getElementById('warning-non-ketua');
        if (warning) {
            warning.style.display = 'block';
        }
    }


    loadAnggotaKelompok();


    setupSearchFunctionality();
    setupViewNavigation();
});


async function loadAnggotaKelompok() {
    const idTugas = getIdTugas();
    
    if (!idTugas) {
        console.error('ID Tugas tidak ditemukan');
        return;
    }
    
    try {
        console.log('Loading anggota for idTugas:', idTugas);
        
        const response = await fetch(`/mahasiswa/api/anggota-kelompok?idTugas=${idTugas}`);
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Gagal memuat anggota');
        }

        console.log('API Response:', data);
        console.log('Number of members:', data.length);
        
        if (data.length > 0) {
            console.log('First member structure:', data[0]);
        }

        currentMembers = data;
        updateMemberDisplay();
        updateMemberCounter();

    } catch (error) {
        console.error('Error loading anggota:', error);
        

        const memberList = document.getElementById('member-list');
        if (memberList) {
            memberList.innerHTML = `
                <div class="error-message">
                    <i class='bx bx-error'></i>
                    <p>Gagal memuat data anggota kelompok</p>
                    <small>${error.message}</small>
                </div>
            `;
        }
    }
}


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
        

        const isLeader = (member.role === 'leader') || (member.user && member.user.role === 'leader');
        const memberName = member.nama || (member.user && member.user.nama) || 'Unknown';
        const memberId = member.idUser || (member.user && member.user.idUser) || '';
        
        console.log('Member data:', {
            name: memberName,
            id: memberId,
            role: member.role,
            isLeader: isLeader
        });
        

        memberItem.innerHTML = `
            <div class="member-info">
                <span class="member-name">${memberName}</span>
                ${isLeader ? '<span class="member-role-badge">Ketua</span>' : ''}
            </div>
            ${!isLeader ? `
                <button class="btn-remove-member" data-id="${memberId}" data-nama="${memberName}" title="Hapus anggota">
                    <i class='bx bx-minus'></i>
                </button>
            ` : ''}
        `;


        if (!isLeader) {
            const removeBtn = memberItem.querySelector('.btn-remove-member');
            if (removeBtn) {
                removeBtn.addEventListener('click', function() {
                    confirmRemoveMember(memberId, memberName);
                });
            }
        }

        memberList.appendChild(memberItem);
    });
}


function updateMemberCounter() {
    const counters = document.querySelectorAll('.member-counter');
    counters.forEach(counter => {
        counter.textContent = `${currentMembers.length}/${maxAnggota} Anggota`;
    });
}

function setupSearchFunctionality() {
    const searchInput = document.getElementById('search-mahasiswa');
    const searchButton = document.getElementById('btn-search');
    const searchResults = document.getElementById('search-results');

    if (!searchInput || !searchButton || !searchResults) {
        console.error('Element pencarian tidak ditemukan');
        return;
    }

    searchButton.addEventListener('click', function(e) {
        e.preventDefault();
        performSearch();
    });


    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            performSearch();
        }
    });

    
    searchInput.addEventListener('input', function() {
        if (searchInput.value.trim() === '') {
            searchResults.innerHTML = '';
            searchResults.style.display = 'none';
        }
    });

   
    document.addEventListener('click', function(e) {
        if (!searchInput.contains(e.target) && 
            !searchButton.contains(e.target) && 
            !searchResults.contains(e.target)) {
            searchResults.style.display = 'none';
        }
    });
}

async function performSearch() {
    const searchInput = document.getElementById('search-mahasiswa');
    const searchResults = document.getElementById('search-results');
    const keyword = searchInput.value.trim();
    const idTugas = getIdTugas();

    if (!keyword) {
        showSearchError('Masukkan nama mahasiswa untuk mencari');
        return;
    }


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

async function addMemberToGroup(mahasiswa) {
    const idTugas = getIdTugas();

   
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

   
        document.getElementById('search-mahasiswa').value = '';
        document.getElementById('search-results').style.display = 'none';

  
        await loadAnggotaKelompok();

        showSuccess(`${mahasiswa.nama} berhasil ditambahkan`);

    } catch (error) {
        console.error('Error adding member:', error);
        showError(error.message || 'Gagal menambahkan anggota');
    }
}


function confirmRemoveMember(idUser, nama) {
    if (confirm(`Apakah Anda yakin ingin menghapus ${nama} dari kelompok?`)) {
        removeMemberFromGroup(idUser, nama);
    }
}

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


        await loadAnggotaKelompok();

        showSuccess(`${nama} berhasil dihapus dari kelompok`);

    } catch (error) {
        console.error('Error removing member:', error);
        showError(error.message || 'Gagal menghapus anggota');
    }
}


function setupViewNavigation() {

    const btnKelolaAnggota = document.getElementById('btn-kelola-anggota');
    if (btnKelolaAnggota) {
        btnKelolaAnggota.addEventListener('click', function() {
            showView('view-anggota');
            loadAnggotaKelompok();
        });
    }

    
    const btnKembaliAnggota = document.getElementById('btn-kembali-anggota');
    if (btnKembaliAnggota) {
        btnKembaliAnggota.addEventListener('click', function() {
            showView('view-deskripsi');
        });
    }

    
    const btnTambahAnggota = document.getElementById('btn-tambah-anggota');
    if (btnTambahAnggota) {
        btnTambahAnggota.addEventListener('click', function() {
            showView('view-pilih-anggota');
        });
    }

    
    const btnSelesaiAnggota = document.getElementById('btn-selesai-anggota');
    if (btnSelesaiAnggota) {
        btnSelesaiAnggota.addEventListener('click', function() {
            showView('view-deskripsi');
        });
    }

    
    const btnKembaliPilih = document.getElementById('btn-kembali-pilih');
    if (btnKembaliPilih) {
        btnKembaliPilih.addEventListener('click', function() {
            showView('view-anggota');
  
            document.getElementById('search-mahasiswa').value = '';
            document.getElementById('search-results').style.display = 'none';
        });
    }

  
    const btnKonfirmasi = document.getElementById('btn-konfirmasi');
    if (btnKonfirmasi) {
        btnKonfirmasi.addEventListener('click', function() {
            showView('view-anggota');

            document.getElementById('search-mahasiswa').value = '';
            document.getElementById('search-results').style.display = 'none';
        });
    }
}


function showView(viewId) {

    const allViews = document.querySelectorAll('.view-content');
    allViews.forEach(view => {
        view.classList.remove('active');
    });


    const selectedView = document.getElementById(viewId);
    if (selectedView) {
        selectedView.classList.add('active');
    }
}


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