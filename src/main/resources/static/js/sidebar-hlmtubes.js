document.addEventListener('DOMContentLoaded', () => {
    // 1. Logic untuk Toggle Sidebar

    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle'); 

    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }

    // 2. Logic untuk Mengubah Highlight (Kelas 'active') Saat Link Diklik

    const navLinks = document.querySelectorAll('.sidebar .nav-link');

    navLinks.forEach(li => {
        const anchor = li.querySelector('a');

        anchor.addEventListener('click', (e) => {
            e.preventDefault(); 
            
            // a. Hapus kelas 'active' dari semua tautan navigasi
            navLinks.forEach(link => {
                link.classList.remove('active');
            });

            // b. Tambahkan kelas 'active' hanya ke elemen <li> yang baru diklik
            li.classList.add('active');
        });
    });
});

