
const searchInput = document.getElementById("search-input");

if (searchInput) {
    searchInput.addEventListener("input", () => {
        const query = searchInput.value.toLowerCase();

        filteredPageItems = masterPageItems.filter(row => {
            const spans = row.querySelectorAll("span");

            const nama = (spans[1]?.textContent || "").toLowerCase();
            const kelas = (spans[2]?.textContent || "").toLowerCase();

            return nama.includes(query) || kelas.includes(query);
        });

        totalPages = Math.max(1, Math.ceil(filteredPageItems.length / itemsPerPage));
        currentPage = 1;

        showPage(currentPage);
    });
}