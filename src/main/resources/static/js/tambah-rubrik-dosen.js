document.addEventListener('DOMContentLoaded', () => {
    // 1. Logic untuk Toggle Sidebar
    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle');
    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }

    // 2. Element DOM
    const tambahKomponenButton = document.querySelector('.tambah-komponen-penilainan');
    const assignmentTableWrapper = document.querySelector('.assignment-table-wrapper');
    const totalBobotDisplay = document.querySelector('.total-bobot .bobot');
    const btnSimpan = document.querySelector('.btn-simpan'); // Diperlukan untuk Simpan/Batal
    const btnBatal = document.querySelector('.btn-batal');   // Diperlukan untuk Simpan/Batal
    const titleRubrik = document.querySelector('.title-rubrik'); // Diperlukan untuk Breadcrumb

    // State Management: Simpan state terakhir yang berhasil/disimpan di awal
    let savedRubricState = [];
    
    // Inisialisasi: Hapus semua assignment-row yang ada di HTML saat load
    const existingRows = assignmentTableWrapper.querySelectorAll('.assignment-row');
    existingRows.forEach(row => row.remove());
    
    // --- State Functions ---
    function captureCurrentState() {
        const rows = assignmentTableWrapper.querySelectorAll('.assignment-row');
        const state = [];
        rows.forEach(row => {
            const inputKomponen = row.querySelector('.col-isi-komponen input');
            const inputBobot = row.querySelector('.col-isi-bobot input');
            const inputKeterangan = row.querySelector('.col-isi-keterangan input');
            
            // Periksa jika input ditemukan sebelum mengambil value
            const komponen = inputKomponen ? inputKomponen.value : '';
            const bobot = inputBobot ? inputBobot.value : '';
            const keterangan = inputKeterangan ? inputKeterangan.value : '';
            
            state.push({ komponen, bobot, keterangan });
        });
        return state;
    }

    // Fungsi Pembantu: Menghapus baris
    function deleteRow(rowElement) {
        if (rowElement && rowElement.parentNode) {
            rowElement.remove();
            updateBobotTotal(); // Selalu perbarui total setelah menghapus
        }
    }

    // --- Utility Functions ---

    function updateBobotTotal() {
        const bobotInputs = assignmentTableWrapper.querySelectorAll('.col-isi-bobot input[type="number"]');
        let total = 0;
        
        bobotInputs.forEach(input => {
            const value = parseFloat(input.value) || 0;
            total += value;
        });

        totalBobotDisplay.textContent = `${total}%`;
        
        // Aturan: Harus 100% agar valid
        if (total !== 100) {
            totalBobotDisplay.style.color = 'red';
            return false;
        } else {
            totalBobotDisplay.style.color = 'green';
            return true;
        }
    }

    function createNewRubricRow() {
        const row = document.createElement('div');
        row.className = 'assignment-row';

        // Kolom Komponen Penilaian
        const colKomponen = document.createElement('div');
        colKomponen.className = 'col-isi-komponen';
        const inputKomponen = document.createElement('input');
        inputKomponen.type = 'text';
        inputKomponen.placeholder = 'Masukkan Komponen Penilaian';
        inputKomponen.style.cssText = 'width: 100%; border: none; outline: none; font-size: 18px;';
        colKomponen.appendChild(inputKomponen);

        // Kolom Bobot (%)
        const colBobot = document.createElement('div');
        colBobot.className = 'col-isi-bobot';
        const inputBobot = document.createElement('input');
        inputBobot.type = 'number';
        inputBobot.min = '0';
        inputBobot.max = '100';
        inputBobot.placeholder = '0';
        inputBobot.style.cssText = 'width: 100%; text-align: center; border: none; outline: none; font-size: 18px;';
        inputBobot.addEventListener('input', updateBobotTotal);
        colBobot.appendChild(inputBobot);

        // Kolom Keterangan
        const colKeterangan = document.createElement('div');
        colKeterangan.className = 'col-isi-keterangan';
        const inputKeterangan = document.createElement('input');
        inputKeterangan.type = 'text';
        inputKeterangan.placeholder = 'Opsional';
        inputKeterangan.style.cssText = 'width: 100%; border: none; outline: none; font-size: 18px;';
        colKeterangan.appendChild(inputKeterangan);
        
        // Kolom Hapus (Tombol Delete)
        const colDelete = document.createElement('div');
        colDelete.className = 'col-delete';
        const deleteButton = document.createElement('button');
        deleteButton.className = 'delete-btn';
        deleteButton.innerHTML = '<i class="bx bx-trash"></i>'; 
        
        // Hapus listener di sini, karena kita akan menggunakan Event Delegation
        
        colDelete.appendChild(deleteButton);
        
        row.appendChild(colKomponen);
        row.appendChild(colBobot);
        row.appendChild(colKeterangan);
        row.appendChild(colDelete); 

        return row;
    }

    // 🔑 FIX TOMBOL DELETE MENGGUNAKAN EVENT DELEGATION
    assignmentTableWrapper.addEventListener('click', (event) => {
        const target = event.target;
        // Cari elemen terdekat dengan class .delete-btn (ini bisa ikon atau tombolnya sendiri)
        const deleteButton = target.closest('.delete-btn');
        
        if (deleteButton) {
            // Dapatkan baris parent dari tombol yang diklik
            const rowToDelete = deleteButton.closest('.assignment-row');
            if (rowToDelete) {
                deleteRow(rowToDelete);
            }
        }
    });

    // --- Event Listeners Aksi Utama ---
    
    // Tombol Tambah Komponen
    if (tambahKomponenButton && assignmentTableWrapper) {
        tambahKomponenButton.addEventListener('click', () => {
            const newRow = createNewRubricRow();
            const tambahKomponenDiv = assignmentTableWrapper.querySelector('.tambah-komponen');
            
            if (tambahKomponenDiv) {
                assignmentTableWrapper.insertBefore(newRow, tambahKomponenDiv);
            } else {
                assignmentTableWrapper.appendChild(newRow);
            }
            updateBobotTotal();
        });
    }

    // Tombol SIMPAN
    if (btnSimpan) {
        btnSimpan.addEventListener('click', () => {
            if (updateBobotTotal()) {
                // 1. Simpan state yang baru (simulasi penyimpanan)
                savedRubricState = captureCurrentState();
                
                // 2. Beri notifikasi
                alert('Rubrik berhasil disimpan!');
                
                // Opsional: Redirect ke halaman Rubrik Penilaian
                // window.location.href = 'rubrik-penilaian-dosen.html'; 
            } else {
                alert('Gagal menyimpan. Total Bobot harus 100%.');
            }
        });
    }
    
    // Tombol BATAL
    if (btnBatal) {
        btnBatal.addEventListener('click', () => {
            // Konfirmasi sebelum kembali
            if (confirm('Apakah Anda yakin ingin membatalkan perubahan? Perubahan yang belum disimpan akan hilang.')) {
                 // Kembali ke halaman sebelumnya
                 window.history.back(); 
            }
        });
    }
    
    // 3. Logic untuk Breadcrumbs (Penilaian > Edit Rubrik)
    if (titleRubrik) {
        // Teks asli dari HTML: <a href="rubrik-penilaian-dosen.html">Penilaian</a> > Edit Rubrik
        const penilainanLink = titleRubrik.querySelector('a');
        const fullText = titleRubrik.textContent;
        const parts = fullText.split('>');
        
        if (penilainanLink) {
            // Pastikan link "Penilaian" berwarna biru dan berfungsi
            penilainanLink.style.color = '#2269a3'; 
            penilainanLink.style.textDecoration = 'none';
        }
    }


    // Inisialisasi awal
    savedRubricState = captureCurrentState();
    updateBobotTotal();
});