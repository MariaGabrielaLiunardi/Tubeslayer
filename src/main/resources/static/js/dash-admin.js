document.addEventListener('DOMContentLoaded', () => {
    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle'); 

    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }

    const navLinks = document.querySelectorAll('.sidebar .nav-link');

    // Highlight URL aktif 
    const currentPath = window.location.pathname;
    navLinks.forEach(li => {
        const anchor = li.querySelector('a');
        if (anchor.getAttribute('href') === currentPath) {
            li.classList.add('active');
        } else {
            li.classList.remove('active');
        }
    });

    // 2. Highlight saat klik 
    navLinks.forEach(li => {
        const anchor = li.querySelector('a');
        anchor.addEventListener('click', () => {
            navLinks.forEach(link => link.classList.remove('active'));
            li.classList.add('active');
        });
    });
});
