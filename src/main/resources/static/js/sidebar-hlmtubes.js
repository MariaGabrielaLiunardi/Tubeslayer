document.addEventListener('DOMContentLoaded', () => {
    // 1. Logic untuk Toggle Sidebar

    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle'); 

    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }

    // 2. Logic untuk Mengubah Highlight (Kelas 'active') 

    const navLinks = document.querySelectorAll('.sidebar .nav-link');

    navLinks.forEach(li => {
        const anchor = li.querySelector('a');

        anchor.addEventListener('click', (e) => {
            //e.preventDefault(); 
            
            // a. Hapus kelas 'active' dari semua bagian navigasi
            navLinks.forEach(link => {
                link.classList.remove('active');
            });

            // b. Tambahkan kelas 'active' hanya ke bagian <li> yang diklik
            li.classList.add('active');
        });
    });
});

