document.addEventListener("DOMContentLoaded", () => {

    /* ============================================================
       ELEMENT DASAR
    ============================================================ */
    const tableView = document.getElementById("table-view");
    const footerView = document.getElementById("footer-view");
    const searchbar = document.getElementById("search-bar");

    const btnAdd = document.getElementById("btn-add");
    const btnDelete = document.getElementById("btn-delete");

    const pilihCara = document.getElementById("pilih-cara");
    const importView = document.getElementById("import-dosen");
    const manualView = document.getElementById("tambah-dosen");

    const btnImport = document.getElementById("btn-import");
    const btnManual = document.getElementById("btn-manual");

    const tambahForm = document.getElementById("tambah-dosen-form");

    const listTitle = document.getElementById("list-title");
    const subTitle = document.getElementById("sub-title");
    const subTitle2 = document.getElementById("sub-title-2");


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

    /* ============================================================
       VIEW: HAPUS DOSEN
    ============================================================ */
    const hapusView = document.getElementById("view-hapus-dosen"); 
    const suggestionsBox = document.getElementById("suggestions");
    const searchInput = document.getElementById("search-input");

    const btnCancelDelete = document.getElementById("btn-cancel-delete");
    const btnConfirmDelete = document.getElementById("btn-confirm-delete");

    const konfirmasiHapus = document.getElementById("konfirmasi-hapus");
    const btnCancelConfirm = document.getElementById("btn-cancel-confirm");
    const btnConfirmDeleteFinal = document.getElementById("btn-confirm-delete-final");

    let selectedDosen = null;
    let daftarDosen = [];

    /* ============================================================
       RESET SEMUA VIEW
    ============================================================ */
    function resetAllView() {
        pilihCara.style.display = "none";
        importView.style.display = "none";
        manualView.style.display = "none";
        hapusView.style.display = "none";
        konfirmasiHapus.style.display = "none";
    }

    resetAllView();

    /* ============================================================
       MASUK PAGES UTAMA
    ============================================================ */
    function showMainPage() {
        resetAllView();
        tableView.style.display = "block";
        footerView.style.display = "flex";
        searchbar.style.display = "block";

        subTitle.textContent = "";
        subTitle2.textContent = "";
    }

    showMainPage();

    /* ============================================================
       ADD DOSEN — PILIH CARA
    ============================================================ */
    btnAdd.addEventListener("click", () => {
        resetAllView();

        tableView.style.display = "none";
        footerView.style.display = "none";
        searchbar.style.display = "none";

        pilihCara.style.display = "flex";

        subTitle.textContent = " > Tambah Dosen";
        subTitle2.textContent = "";
    });

    /* ============================================================
       IMPORT
    ============================================================ */
    btnImport.addEventListener("click", () => {
        pilihCara.style.display = "none";
        importView.style.display = "flex";

        subTitle2.textContent = " > Import";
    });

    /* ============================================================
       TAMBAH MANUAL
    ============================================================ */
    btnManual.addEventListener("click", () => {
        pilihCara.style.display = "none";
        manualView.style.display = "flex";

        subTitle2.textContent = " > Tambah Baru";
    });

    /* ============================================================
       KEMBALI KE LIST VIEW
    ============================================================ */
    listTitle.addEventListener("click", showMainPage);

    /* ============================================================
       TAMBAH MANUAL → KE TABEL
    ============================================================ */
    function tambahKeTabel(nip, nama, mk, status) {
        const rows = tableView.querySelectorAll(".data-row").length;

        const div = document.createElement("div");
        div.classList.add("data-row");
        div.innerHTML = `
            <span>${rows + 1}</span>
            <span>${nip}</span>
            <span>${nama}</span>
            <span>${mk}</span>
            <span>${status == 1 ? "Aktif" : "Nonaktif"}</span>
        `;
        tableView.appendChild(div);
    }

    tambahForm.addEventListener("submit", (e) => {
        e.preventDefault();

        let nip = document.getElementById("nip-dosen").value;
        let nama = document.getElementById("nama-dosen").value;
        let mk = document.getElementById("matkul-dosen").value;
        let status = document.getElementById("status-dosen").value;

        tambahKeTabel(nip, nama, mk, status);

        tambahForm.reset();
        showMainPage();
    });

    /* ============================================================
       UPDATE LIST DOSEN (AUTOCOMPLETE)
    ============================================================ */
    function updateDosenList() {
        daftarDosen = [];
        const rows = tableView.querySelectorAll(".data-row");
        rows.forEach(r => {
            const namaCell = r.children[2];
            if (namaCell) {
                daftarDosen.push(namaCell.textContent.trim());
            }
        });
    }


    updateDosenList();

    /* ============================================================
       MASUK VIEW HAPUS
    ============================================================ */
    btnDelete.addEventListener("click", () => {
        updateDosenList();

        tableView.style.display = "none";
        footerView.style.display = "none";
        searchbar.style.display = "none";

        hapusView.style.display = "flex";

        subTitle.textContent = " > Hapus Dosen";
    });

    /* ============================================================
       AUTOCOMPLETE
    ============================================================ */
    searchInput.addEventListener("input", () => {
        const keyword = searchInput.value.toLowerCase();
        suggestionsBox.innerHTML = "";

        if (keyword.length === 0) {
            suggestionsBox.style.display = "none";
            return;
        }

        const hasil = daftarDosen.filter(n => n.toLowerCase().includes(keyword));

        if (hasil.length === 0) {
            suggestionsBox.style.display = "none";
            return;
        }

        suggestionsBox.style.display = "block";

        hasil.forEach(nama => {
            const li = document.createElement("li");
            li.textContent = nama;

            li.addEventListener("click", () => {
                searchInput.value = nama;
                selectedDosen = nama;
                suggestionsBox.style.display = "none";
            });

            suggestionsBox.appendChild(li);
        });
    });

    /* ============================================================
       BATAL HAPUS
    ============================================================ */
    btnCancelDelete.addEventListener("click", showMainPage);

    /* ============================================================
       MASUK VIEW KONFIRMASI
    ============================================================ */
    btnConfirmDelete.addEventListener("click", () => {
        if (!selectedDosen) {
            alert("Pilih dosen dulu!");
            return;
        }

        hapusView.style.display = "none";
        konfirmasiHapus.style.display = "flex";
    });

    /* ============================================================
       BATAL KONFIRMASI
    ============================================================ */
    btnCancelConfirm.addEventListener("click", showMainPage);

    /* ============================================================
       HAPUS FINAL
    ============================================================ */
    btnConfirmDeleteFinal.addEventListener("click", () => {
        if (!selectedDosen) return;

        const rows = tableView.querySelectorAll(".data-row");
        rows.forEach(row => {
            if (row.children[2].textContent.trim() === selectedDosen) {
                row.remove();
            }
        });

        selectedDosen = null;
        konfirmasiHapus.style.display = "none";
        showMainPage();
    });
});
