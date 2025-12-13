 // Simple pagination setup
 
    document.addEventListener('DOMContentLoaded', function() {
        const rows = document.querySelectorAll('#table-body tr');
        const pageSize = 3;
        let currentPage = 1;
        const totalPages = Math.ceil(rows.length / pageSize);

        function renderPage() {
            rows.forEach((row, index) => {
                row.style.display = (index >= (currentPage - 1) * pageSize && index < currentPage * pageSize) ? '' : 'none';
            });
            document.getElementById('page-info').textContent = currentPage + ' / ' + totalPages;
            document.getElementById('prev-page').disabled = currentPage === 1;
            document.getElementById('next-page').disabled = currentPage === totalPages;
        }

        document.getElementById('prev-page').addEventListener('click', function() {
            if (currentPage > 1) {
                currentPage--;
                renderPage();
            }
        });

        document.getElementById('next-page').addEventListener('click', function() {
            if (currentPage < totalPages) {
                currentPage++;
                renderPage();
            }
        });

        // Search functionality
        document.getElementById('search-input').addEventListener('keyup', function(e) {
            const query = e.target.value.toLowerCase();
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(query) ? '' : 'none';
            });
        });

        // Make rows clickable with cursor pointer
        rows.forEach(row => {
            row.style.cursor = 'pointer';
        });

        renderPage();
    });