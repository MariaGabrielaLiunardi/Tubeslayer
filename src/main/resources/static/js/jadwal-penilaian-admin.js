document.addEventListener('DOMContentLoaded', () => {

    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle'); 

    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }

    const navLinks = document.querySelectorAll('.sidebar .nav-link');

    navLinks.forEach(li => {
        const anchor = li.querySelector('a');

        anchor.addEventListener('click', (e) => {
            e.preventDefault(); 
            
            navLinks.forEach(link => {
                link.classList.remove('active');
            });

            li.classList.add('active');
        });
    });
});