document.addEventListener('DOMContentLoaded', () => {
    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle'); 

    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }

    const navLinks = document.querySelectorAll('.sidebar .nav-link');
    const currentPath = window.location.pathname; // contoh: "/admin/dashboard/detail/123"

    // Highlight sesuai URL aktif (jalan saat halaman dimuat)
    navLinks.forEach(li => {
        const anchor = li.querySelector('a');
        const href = anchor.getAttribute('href');
        if (currentPath.startsWith(href)) {
            li.classList.add('active');
        } else {
            li.classList.remove('active');
        }
    });

    // Highlight saat klik (opsional)
    navLinks.forEach(li => {
        const anchor = li.querySelector('a');
        anchor.addEventListener('click', () => {
            navLinks.forEach(link => link.classList.remove('active'));
            li.classList.add('active');
        });
    });
});
