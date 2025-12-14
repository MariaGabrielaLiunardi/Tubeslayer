document.addEventListener('DOMContentLoaded', () => {

    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle'); 

    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }

    const btnMatkul = document.getElementById("btnMatkul");
    const btnDosen = document.getElementById("btnDosen");
    const btnMahasiswa = document.getElementById("btnMaha");

    if (btnMatkul) {
        btnMatkul.addEventListener("click", () => {
            window.location.href = "/admin/kelola-mata-kuliah"; 
        });
    }

    if (btnDosen) {
        btnDosen.addEventListener("click", () => {
            window.location.href = "/admin/kelola-dosen"; 
        });
    }

    if (btnMahasiswa) {
        btnMahasiswa.addEventListener("click", () => {
            window.location.href = "/admin/kelola-mahasiswa"; 
        });
    }

    const navLinks = document.querySelectorAll('.sidebar .nav-link');
    const currentPath = window.location.pathname; 

    navLinks.forEach(li => {
        const anchor = li.querySelector('a');
        const href = anchor.getAttribute('href');

        if (currentPath === href || 
            (href === "/admin/menu-awal-ad" && currentPath.startsWith("/admin/kelola"))) {
            li.classList.add('active');
        } else {
            li.classList.remove('active');
        }
    });
});