document.addEventListener('DOMContentLoaded', () => {
    
    // ========== DATA & STATE ==========
    
    // TODO: AMBIL DATA USER DARI SESSION/DATABASE
    let currentUser = {
        nama: "",
        nim: "",
        isKetua: false
    };
    
    // TODO: FUNGSI UNTUK LOAD CURRENT USER (Menggunakan .then())
    function loadCurrentUser() {
        // CARA INTEGRASI DENGAN DATABASE:
        // fetch('/api/user/current', {
        //     method: 'GET',
        //     headers: {
        //         'Content-Type': 'application/json',
        //     }
        // })
        // .then(response => response.json())
        // .then(data => {
        //     // Assign data dari database
        //     currentUser = {
        //         nama: data.nama,
        //         nim: data.nim,
        //         isKetua: data.isKetua
        //     };
        //     
        //     // Update UI
        //     const userNameElement = document.getElementById('current-user-name');
        //     if (userNameElement) {
        //         userNameElement.textContent = currentUser.nama;
        //     }
        //     
        //     console.log('Current user loaded:', currentUser);
        // })
        // .catch(error => {
        //     console.error('Error loading current user:', error);
        //     alert('Gagal memuat data user. Silakan refresh halaman.');
        // });
        
        // TEMPORARY: Untuk testing tanpa backend (HAPUS SAAT INTEGRASI)
        currentUser = {
            nama: "Keisha Neira Jocelyn",
            nim: "6182301001",
            isKetua: true // UBAH KE false UNTUK TEST NON-KETUA
        };
        console.log('Current user loaded:', currentUser);
    }
    
    // TODO: AMBIL DATA ANGGOTA KELOMPOK DARI DATABASE
    let kelompokAnggota = [];
    
    // TODO: FUNGSI UNTUK LOAD ANGGOTA KELOMPOK (Menggunakan .then())
    function loadKelompokAnggota() {
        // CARA INTEGRASI DENGAN DATABASE:
        // const kelompokId = 1; // Ambil dari currentUser atau URL
        // fetch(`/api/kelompok/${kelompokId}/anggota`, {
        //     method: 'GET',
        //     headers: {
        //         'Content-Type': 'application/json',
        //     }
        // })
        // .then(response => response.json())
        // .then(data => {
        //     // Assign data dari database
        //     kelompokAnggota = data; // atau data.anggota
        //     
        //     // Render setelah load
        //     renderMemberList();
        //     console.log('Kelompok anggota loaded:', kelompokAnggota);
        // })
        // .catch(error => {
        //     console.error('Error loading kelompok anggota:', error);
        //     alert('Gagal memuat data anggota kelompok.');
        // });
        
        // TEMPORARY: Untuk testing tanpa backend (HAPUS SAAT INTEGRASI)
        kelompokAnggota = [
            {
                nama: currentUser.nama,
                nim: currentUser.nim,
                role: "Ketua"
            }
        ];
        renderMemberList();
        console.log('Kelompok anggota loaded:', kelompokAnggota);
    }
    
    // Selected member sementara dari search
    let selectedMember = null;
    
    // ========== DOM ELEMENTS ==========
    
    // Views
    const viewDeskripsi = document.getElementById('view-deskripsi');
    const viewAnggota = document.getElementById('view-anggota');
    const viewPilihAnggota = document.getElementById('view-pilih-anggota');
    
    // Buttons
    const btnKelolaAnggota = document.getElementById('btn-kelola-anggota');
    const btnKembaliAnggota = document.getElementById('btn-kembali-anggota');
    const btnTambahAnggota = document.getElementById('btn-tambah-anggota');
    const btnSelesaiAnggota = document.getElementById('btn-selesai-anggota');
    const btnKembaliPilih = document.getElementById('btn-kembali-pilih');
    const btnKonfirmasi = document.getElementById('btn-konfirmasi');
    const btnSearch = document.getElementById('btn-search');
    
    // Other elements
    const memberList = document.getElementById('member-list');
    const memberCounter = document.getElementById('member-counter');
    const memberCounterPilih = document.getElementById('member-counter-pilih');
    const warningNonKetua = document.getElementById('warning-non-ketua');
    const searchInput = document.getElementById('search-mahasiswa');
    const searchResults = document.getElementById('search-results');
    
    // ========== FUNCTIONS ==========
    
    // Switch between views
    function showView(viewToShow) {
        viewDeskripsi.classList.remove('active');
        viewAnggota.classList.remove('active');
        viewPilihAnggota.classList.remove('active');
        viewToShow.classList.add('active');
    }
    
    // Render member list
    function renderMemberList() {
        memberList.innerHTML = '';
        
        kelompokAnggota.forEach((member) => {
            const memberItem = document.createElement('div');
            memberItem.className = 'member-item';
            
            memberItem.innerHTML = `
                <div class="member-info">
                    <span class="member-name">${member.nama}</span>
                    ${member.role ? `<span class="member-badge">${member.role}</span>` : ''}
                </div>
            `;
            
            memberList.appendChild(memberItem);
        });
        
        updateCounter();
    }
    
    // Update member counter
    function updateCounter() {
        const count = kelompokAnggota.length;
        const counterText = `${count}/5 Anggota`;
        memberCounter.textContent = counterText;
        memberCounterPilih.textContent = counterText;
    }
    
    // Setup view anggota based on user role
    function setupViewAnggota() {
        if (!currentUser.isKetua) {
            btnTambahAnggota.disabled = true;
            btnTambahAnggota.style.backgroundColor = '#9e9e9e';
            btnSelesaiAnggota.disabled = true;
            btnSelesaiAnggota.style.backgroundColor = '#9e9e9e';
            warningNonKetua.style.display = 'block';
        } else {
            btnTambahAnggota.disabled = false;
            btnTambahAnggota.style.backgroundColor = '#000';
            btnSelesaiAnggota.disabled = false;
            btnSelesaiAnggota.style.backgroundColor = '#000';
            warningNonKetua.style.display = 'none';
        }
    }
    
    // TODO: SEARCH MAHASISWA DARI DATABASE (Menggunakan .then())
    function searchMahasiswa(query) {
        if (!query || query.trim() === '') {
            searchResults.classList.remove('active');
            return;
        }
        
        // CARA INTEGRASI DENGAN DATABASE:
        // fetch(`/api/mahasiswa/search?q=${encodeURIComponent(query)}`, {
        //     method: 'GET',
        //     headers: {
        //         'Content-Type': 'application/json',
        //     }
        // })
        // .then(response => response.json())
        // .then(data => {
        //     // Filter mahasiswa yang sudah jadi anggota
        //     const existingNims = kelompokAnggota.map(m => m.nim);
        //     const filtered = data.filter(mhs => !existingNims.includes(mhs.nim));
        //     
        //     renderSearchResults(filtered);
        // })
        // .catch(error => {
        //     console.error('Error searching mahasiswa:', error);
        //     searchResults.innerHTML = '<div class="no-results">Error saat mencari mahasiswa</div>';
        //     searchResults.classList.add('active');
        // });
        
        // TEMPORARY: Simulasi hasil search (HAPUS SAAT INTEGRASI)
        const mahasiswaDatabase = [
            { nama: "Marsella Moretta", nim: "6182301058" },
            { nama: "Ahmad Rizki", nim: "6182301002" },
            { nama: "Siti Nurhaliza", nim: "6182301003" },
            { nama: "Budi Santoso", nim: "6182301004" },
            { nama: "Dewi Lestari", nim: "6182301005" },
            { nama: "Eko Prasetyo", nim: "6182301006" },
            { nama: "Fitri Handayani", nim: "6182301007" },
            { nama: "Gilang Ramadhan", nim: "6182301008" },
        ];
        
        const lowerQuery = query.toLowerCase();
        const existingNims = kelompokAnggota.map(m => m.nim);
        const filtered = mahasiswaDatabase.filter(mhs => {
            const matchName = mhs.nama.toLowerCase().includes(lowerQuery);
            const matchNim = mhs.nim.includes(lowerQuery);
            const notMember = !existingNims.includes(mhs.nim);
            return (matchName || matchNim) && notMember;
        });
        
        renderSearchResults(filtered);
    }
    
    // Render search results
    function renderSearchResults(results) {
        searchResults.innerHTML = '';
        
        if (results.length === 0) {
            searchResults.innerHTML = '<div class="no-results">Tidak ada hasil ditemukan</div>';
            searchResults.classList.add('active');
            return;
        }
        
        results.forEach(mhs => {
            const resultItem = document.createElement('div');
            resultItem.className = 'search-result-item';
            resultItem.innerHTML = `
                <div class="search-result-name">${mhs.nama}</div>
                <div class="search-result-nim">${mhs.nim}</div>
            `;
            
            resultItem.addEventListener('click', () => {
                selectMahasiswa(mhs);
            });
            
            searchResults.appendChild(resultItem);
        });
        
        searchResults.classList.add('active');
    }
    
    // Select mahasiswa from search results
    function selectMahasiswa(mahasiswa) {
        selectedMember = mahasiswa;
        searchInput.value = `${mahasiswa.nama} - ${mahasiswa.nim}`;
        searchResults.classList.remove('active');
    }
    
    // TODO: ADD MEMBER KE DATABASE (Menggunakan .then())
    function addMember() {
        if (!selectedMember) {
            alert('Silakan pilih mahasiswa terlebih dahulu');
            return;
        }
        
        if (kelompokAnggota.length >= 5) {
            alert('Kelompok sudah penuh (maksimal 5 anggota)');
            return;
        }
        
        const isAlreadyMember = kelompokAnggota.some(m => m.nim === selectedMember.nim);
        if (isAlreadyMember) {
            alert('Mahasiswa ini sudah menjadi anggota kelompok');
            return;
        }
        
        // CARA INTEGRASI DENGAN DATABASE:
        // const kelompokId = 1; // Ambil dari currentUser atau state
        // fetch(`/api/kelompok/${kelompokId}/anggota`, {
        //     method: 'POST',
        //     headers: {
        //         'Content-Type': 'application/json',
        //     },
        //     body: JSON.stringify({
        //         nim: selectedMember.nim
        //     })
        // })
        // .then(response => response.json())
        // .then(data => {
        //     if (!data.success) {
        //         alert(data.message || 'Gagal menambahkan anggota');
        //         return;
        //     }
        //     
        //     // Reload data dari database setelah berhasil
        //     loadKelompokAnggota();
        //     
        //     // Reset and go back
        //     selectedMember = null;
        //     searchInput.value = '';
        //     showView(viewAnggota);
        //     
        //     console.log('Member added successfully');
        // })
        // .catch(error => {
        //     console.error('Error adding member:', error);
        //     alert('Gagal menambahkan anggota. Silakan coba lagi.');
        // });
        
        // TEMPORARY: Add to local array (HAPUS SAAT INTEGRASI)
        kelompokAnggota.push({
            nama: selectedMember.nama,
            nim: selectedMember.nim,
            role: null
        });
        
        selectedMember = null;
        searchInput.value = '';
        renderMemberList();
        showView(viewAnggota);
        console.log('Member added successfully');
    }
    
    // ========== EVENT LISTENERS ==========
    
    // Button: Kelola Anggota
    btnKelolaAnggota.addEventListener('click', () => {
        loadKelompokAnggota();
        setupViewAnggota();
        showView(viewAnggota);
    });
    
    // Button: Kembali (dari view anggota)
    btnKembaliAnggota.addEventListener('click', () => {
        showView(viewDeskripsi);
    });
    
    // Button: Tambah
    btnTambahAnggota.addEventListener('click', () => {
        if (kelompokAnggota.length >= 5) {
            alert('Kelompok sudah penuh (maksimal 5 anggota)');
            return;
        }
        searchInput.value = '';
        selectedMember = null;
        searchResults.classList.remove('active');
        showView(viewPilihAnggota);
    });
    
    // Button: Selesai
    btnSelesaiAnggota.addEventListener('click', () => {
        showView(viewDeskripsi);
    });
    
    // Button: Kembali (dari view pilih)
    btnKembaliPilih.addEventListener('click', () => {
        selectedMember = null;
        searchInput.value = '';
        searchResults.classList.remove('active');
        showView(viewAnggota);
    });
    
    // Button: Konfirmasi
    btnKonfirmasi.addEventListener('click', () => {
        addMember();
    });
    
    // Button: Search
    btnSearch.addEventListener('click', () => {
        searchMahasiswa(searchInput.value);
    });
    
    // Input: Search on Enter
    searchInput.addEventListener('keyup', (e) => {
        if (e.key === 'Enter') {
            searchMahasiswa(searchInput.value);
        }
    });
    
    // Click outside to close search results
    document.addEventListener('click', (e) => {
        if (!searchResults.contains(e.target) && 
            !searchInput.contains(e.target) && 
            !btnSearch.contains(e.target)) {
            searchResults.classList.remove('active');
        }
    });
    
    // ========== INITIALIZATION ==========
    loadCurrentUser();
    console.log('Kelola Anggota module loaded');
});