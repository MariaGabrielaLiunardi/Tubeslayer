document.addEventListener("DOMContentLoaded", () => {

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

        anchor.addEventListener("click", (e) => {
            e.preventDefault();

            navLinks.forEach(link => link.classList.remove("active"));
            li.classList.add("active");
        });
    });


    /* ===============================
       3. SWITCH VIEW
    ================================ */

    const btnAdd = document.getElementById("btn-add");
    const pilihCara = document.getElementById("pilih-cara");

    const tableView = document.getElementById("table-view");
    const footerView = document.getElementById("footer-view");
    const searchbar = document.getElementById("search-bar");

    const subTitle = document.getElementById("sub-title");
    const listTitle = document.getElementById("list-title");

    const btnImport = document.getElementById("btn-import");
    const subTitle2 = document.getElementById("sub-title-2");
    const importView = document.getElementById("import-matkul"); 
    const btnManual = document.getElementById("btn-manual");
    const manualView = document.getElementById("tambah-matkul");    
    const tambahForm = document.getElementById("tambah-matkul-form");

    importView.style.display = "none";
    manualView.style.display = "none";
    tambahForm.style.display = "none";


    /* ============ MASUK VIEW PILIH CARA ============ */
    btnAdd.addEventListener("click", () => {
        tableView.style.display = "none";
        footerView.style.display = "none";
        searchbar.style.display = "none";

        pilihCara.style.display = "flex";
        importView.style.display = "none";

        subTitle.textContent = " > Tambah Mata Kuliah";
        subTitle2.textContent = ""; // kosongkan import
    });

    /* ============ KLIK IMPORT DI PILIH CARA ============ */
    btnImport.addEventListener("click", () => {
        tableView.style.display = "none";
        footerView.style.display = "none";
        searchbar.style.display = "none";

        pilihCara.style.display = "none";
        importView.style.display = "flex";

        subTitle.textContent = " > Tambah Mata Kuliah";
        subTitle2.textContent = " > Import";
    });

    btnManual.addEventListener("click", () => {
        tableView.style.display = "none";
        footerView.style.display = "none";
        searchbar.style.display = "none";

        manualView.style.display = "flex";
        pilihCara.style.display = "none";
        importView.style.display = "none";
        subTitle.textContent = " > Tambah Mata Kuliah";
        subTitle2.textContent = " > Tambah Baru";
        tambahForm.style.display = "flex";
    });

    /* ============ KLIK “DAFTAR MATA KULIAH” ============ */
    listTitle.addEventListener("click", () => {
        pilihCara.style.display = "none";
        importView.style.display = "none";

        tableView.style.display = "block";
        footerView.style.display = "flex";
        searchbar.style.display = "block";

        subTitle.textContent = "";
        subTitle2.textContent = "";

    });

   /* ===============================
   4. UPLOAD FILE → MASUK TABEL 
================================ */
const fileInput = document.getElementById("file-input");
const uploadBtn = document.getElementById("btn-pilih-file");

uploadBtn.addEventListener("click", () => {
    const file = fileInput.files[0];
    if (!file) return alert("Pilih file dulu ya!");

    const reader = new FileReader();

    reader.onload = function(e) {
        const data = new Uint8Array(e.target.result);
        const workbook = XLSX.read(data, { type: "array" });

        // Ambil sheet pertama
        const sheetName = workbook.SheetNames[0];
        const sheet = workbook.Sheets[sheetName];

        // Konversi ke JSON
        const rows = XLSX.utils.sheet_to_json(sheet, { header: 1 });

        // Bersihkan tabel
        const tableView = document.getElementById("table-view");
        tableView.innerHTML = `
            <div class="table-header-row">
                <span>No</span>
                <span>Kode</span>
                <span>Nama</span>
                <span>SKS</span>
                <span>Status</span>
            </div>
        `;

        // Masukkan ke tabel
        rows.slice(1).forEach((row, index) => {
            const no = index + 1;
            const kode = row[0] || "-";
            const nama = row[1] || "-";
            const sks = row[2] || "0";
            const status = row[3] || "0";

            const div = document.createElement("div");
            div.classList.add("data-row");
            div.innerHTML = `
                <span>${no}</span>
                <span>${kode}</span>
                <span>${nama}</span>
                <span>${sks}</span>
                <span>${status}</span>
            `;
            tableView.appendChild(div);
        });

        // Kembali ke view 1
        importView.style.display = "none";
        tableView.style.display = "block";
        footerView.style.display = "flex";
        searchbar.style.display = "block";

        subTitle.textContent = "";
    };

    reader.readAsArrayBuffer(file);
});

    //fungsi tambah ke tabel
    function tambahKeTabel(kode, nama, sks, status) {
        const tableView = document.getElementById("table-view");

        // hitung jumlah data-row untuk menentukan nomor
        const currentRows = tableView.querySelectorAll(".data-row").length;
        const no = currentRows + 1;

        // bikin elemen baris baru
        const div = document.createElement("div");
        div.classList.add("data-row");

       div.innerHTML = `
            <span>${no}</span>
            <span>${kode}</span>
            <span>${nama}</span>
            <span>${sks}</span>  
            <span>${status}</span>
        `;

        // masukin ke tabel
        tableView.appendChild(div);
    }

    tambahForm.addEventListener("submit", function(e) {
        e.preventDefault();

        let kode = document.getElementById("kode-matkul").value;
        let nama = document.getElementById("nama-matkul").value;
        let sks = document.getElementById("sks-matkul").value;
        let status = document.getElementById("status-matkul").value;

        tambahKeTabel(kode, nama,sks, status);

        this.reset();
        manualView.style.display = "none";
        listTitle.click(); // balik ke view 1

    });

   /* ===============================
   6. HAPUS MATA KULIAH  
================================ */


    //Bagian Hapus Mata kuliah
    const mataKuliah = [
    "Algoritma",
    "Pemrograman Web",
    "Dasar Pemrograman",
    "Matematika Diskrit",
    "Jaringan Komputer",
    "Kecerdasan Buatan"
    ];
    
    const searchHapus = document.getElementById("hapus-mata-kuliah");
    const input = document.getElementById("search-input");
    const suggestionsBox = document.getElementById("suggestions");
    const btnHapus = document.getElementById("btn-delete");
    const btnBatal = document.getElementById("btn-cancel-delete");
    let selectedMK = null;
    searchHapus.style.display = "none";

    btnHapus.addEventListener("click", () => {
        tableView.style.display = "none";
        footerView.style.display = "none";
        searchbar.style.display = "none";   
        searchHapus.style.display = "flex";
        subTitle.textContent = " > Hapus Mata Kuliah";
    });

    btnBatal.addEventListener("click", () => {
        searchHapus.style.display = "none";
        tableView.style.display = "block";
        footerView.style.display = "flex";
        searchbar.style.display = "block";
    });

    input.addEventListener("input", () => {
        const keyword = input.value.toLowerCase();
        suggestionsBox.innerHTML = "";

    if (keyword.length === 0) {
        suggestionsBox.style.display = "none";
        return;
    }

    const hasil = mataKuliah.filter(mk =>
        mk.toLowerCase().includes(keyword)
    );

    if (hasil.length === 0) {
        suggestionsBox.style.display = "none";
        return;
    }

    suggestionsBox.style.display = "block";

        hasil.forEach(mk => {
            const li = document.createElement("li");
            li.textContent = mk;
            li.addEventListener("click", () => {
                input.value = mk;
                selectedMK = mk; // simpan matkul yg dipilih
                suggestionsBox.style.display = "none";
            });
            suggestionsBox.appendChild(li);
        });
    });


    //KALAU KLIK HAPUS DI VIEW 5, BERIKAN HALAMAN KONFIRMASI VIEW 6
    const btnDeleteFinal = document.getElementById("btn-confirm-delete-final");
    const btnpilihDelete = document.getElementById("btn-confirm-delete");
    const confirmView = document.getElementById("konfirmasi-hapus");
    confirmView.style.display = "none";

    btnpilihDelete.addEventListener("click", () => {
       tableView.style.display = "none";
        footerView.style.display = "none";
        searchbar.style.display = "none";   
        searchHapus.style.display = "none";
        subTitle.textContent = " > Hapus Mata Kuliah";
        confirmView.style.display = "flex";
    });

    btnDeleteFinal.addEventListener("click", () => {
        if (selectedMK) {
            // Hapus mata kuliah dari array
            const index = mataKuliah.indexOf(selectedMK);
            if (index > -1) {
                mataKuliah.splice(index, 1);
            }       
            // Hapus dari tabel (jika ada)
            const rows = tableView.querySelectorAll(".data-row");
            rows.forEach(row => {
                const namaMK = row.children[2].textContent;
                if (namaMK === selectedMK) {
                    tableView.removeChild(row);
                }
            });

            // Reset dan kembali ke view utama
            selectedMK = null;
            input.value = "";
            confirmView.style.display = "none";
            tableView.style.display = "block";
            footerView.style.display = "flex";
            searchbar.style.display = "block";   
            subTitle.textContent = "";
        }
    });


});