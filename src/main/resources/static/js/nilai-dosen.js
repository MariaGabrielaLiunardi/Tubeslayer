document.addEventListener('DOMContentLoaded', () => {
    // 1. Logic untuk Toggle Sidebar
    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle');

    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }

    // 2. Logout handler
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', function() {
            if (confirm('Apakah Anda yakin ingin logout?')) {
                window.location.href = '/logout';
            }
        });
    }

    // 3. Search functionality
    const searchInput = document.getElementById('searchInput');
    const tableRows = document.querySelectorAll('.peserta-table-wrapper tbody tr');
    
    if (searchInput) {
        searchInput.addEventListener('keyup', function() {
            const keyword = this.value.toLowerCase();
            tableRows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(keyword) ? '' : 'none';
            });
        });
    }
});
