document.addEventListener('DOMContentLoaded', () => {
    // 1. Logic Toggle Sidebar

    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle'); 

    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }

    // 2. Logic Mengubah Highlight (Kelas 'active')

    const navLinks = document.querySelectorAll('.sidebar .nav-link');

    navLinks.forEach(li => {
        const anchor = li.querySelector('a');

        anchor.addEventListener('click', (e) => {
            //e.preventDefault(); 
            
            // a. Hapus kelas 'active' dari navigasi
            navLinks.forEach(link => {
                link.classList.remove('active');
            });

            // b. Tambahkan kelas 'active' ke dalam <li> yang baru diklik
            li.classList.add('active');
        });
    });
});

