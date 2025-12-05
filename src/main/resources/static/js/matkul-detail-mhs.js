document.addEventListener("DOMContentLoaded", () => {
    
    const mkTabDiv = document.querySelector('.mk-tab');
    const mataKuliahId = mkTabDiv ? mkTabDiv.getAttribute('data-mk-kode') : null;

    // --- Logika Tab Navigation (Navigasi ke Daftar Peserta) ---
    const tabs = document.querySelectorAll('.mk-tab button');
    
    tabs.forEach(tab => {
        const tabTarget = tab.getAttribute('data-tab-target');

        // Navigasi ke Daftar Peserta (endpoint Mahasiswa)
        if (tabTarget === 'peserta' && mataKuliahId) {
            tab.addEventListener('click', () => {
                window.location.href = `/mahasiswa/matkul-peserta?kodeMk=${encodeURIComponent(mataKuliahId)}`;
            });
        }
        // Navigasi ke halaman Nilai (jika endpoint sudah tersedia)
        // if (tabTarget === 'nilai' && mataKuliahId) { ... }
    });

    // --- Logika Logout ---
        const handleLogout = () => {
        console.log("Melakukan proses logout..."); 
        // Menggunakan fetch POST untuk memicu logout Spring Security
        fetch('/logout', { method: 'POST' }) 
            .then(() => {
                 // Setelah berhasil, redirect ke halaman utama
                 window.location.href = '/'; 
            })
            .catch(error => {
                 console.error("Logout gagal:", error);
                 // Fallback: tetap redirect meskipun fetch gagal
                 window.location.href = '/'; 
            });
    };

    const logoutButton = document.getElementById('logoutButton'); 
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    } 
    
});