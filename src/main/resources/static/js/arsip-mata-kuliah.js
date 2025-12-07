// ------------------------------------------------------------------
// --- Pagination untuk elemen DIV .data-row (bukan tabel) ---
// ------------------------------------------------------------------

// Ambil semua data-row (master data)
const masterPageItems = Array.from(document.querySelectorAll(".data-row"));

// Jika tidak ada data, hentikan script
if (masterPageItems.length === 0) {
    console.warn("Tidak ada .data-row ditemukan.");
}

// Variabel untuk menyimpan item yang sedang difilter
let filteredPageItems = masterPageItems;

// Elemen Pagination
const prevButton = document.getElementById('prev-page');
const nextButton = document.getElementById('next-page');
const pageInfoSpan = document.getElementById('current-page');

const itemsPerPage = 3;
let currentPage = 1;

// Hitung ulang total halaman
let totalPages = Math.max(1, Math.ceil(filteredPageItems.length / itemsPerPage));

// --- Fungsi Menampilkan Halaman ---
const showPage = (page) => {

    const start = (page - 1) * itemsPerPage;
    const end = page * itemsPerPage;

    // Sembunyikan semua (master list)
    masterPageItems.forEach(item => {
        item.style.display = "none";
    });

    // Tampilkan hanya item filter untuk halaman ini
    filteredPageItems.forEach((item, index) => {
        if (index >= start && index < end) {
            item.style.display = "block"; // karena DIV, bukan TR
        }
    });

    // Update info halaman
    pageInfoSpan.textContent = `${currentPage} / ${totalPages}`;

    // Disable prev/next kalau mentok
    prevButton.disabled = currentPage === 1;
    nextButton.disabled = currentPage === totalPages;
};

// --- Tombol Prev ---
prevButton.addEventListener("click", () => {
    if (currentPage > 1) {
        currentPage--;
        showPage(currentPage);
    }
});

// --- Tombol Next ---
nextButton.addEventListener("click", () => {
    if (currentPage < totalPages) {
        currentPage++;
        showPage(currentPage);
    }
});

// --- Initial Load ---
showPage(currentPage);

// ------------------------------------------------------------------
// --- Logika Search / Filter untuk .data-row ---
// ------------------------------------------------------------------

const searchInput = document.getElementById("search-input");

if (searchInput) {
    searchInput.addEventListener("input", () => {
        const query = searchInput.value.toLowerCase();

        // filter berdasar span ke-2 (nama) dan span ke-3 (kelas)
        filteredPageItems = masterPageItems.filter(row => {
            const spans = row.querySelectorAll("span");

            const nama = (spans[1]?.textContent || "").toLowerCase();
            const kelas = (spans[2]?.textContent || "").toLowerCase();

            return nama.includes(query) || kelas.includes(query);
        });

        // Hitung ulang total halaman
        totalPages = Math.max(1, Math.ceil(filteredPageItems.length / itemsPerPage));
        currentPage = 1;

        showPage(currentPage);
    });
}
