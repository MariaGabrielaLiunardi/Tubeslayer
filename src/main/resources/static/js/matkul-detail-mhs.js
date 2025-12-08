document.addEventListener("DOMContentLoaded", () => {
    
    const mkTabDiv = document.querySelector('.mk-tab');
    const mataKuliahId = mkTabDiv ? mkTabDiv.getAttribute('data-mk-kode') : null;

    const tabs = document.querySelectorAll('.mk-tab button');
    
    tabs.forEach(tab => {
        const tabTarget = tab.getAttribute('data-tab-target');

        if (tabTarget === 'peserta' && mataKuliahId) {
            tab.addEventListener('click', () => {
                window.location.href = `/mahasiswa/matkul-peserta?kodeMk=${encodeURIComponent(mataKuliahId)}`;
            });
        }
        
    });

    // Logout
    
        const handleLogout = () => {
        console.log("Melakukan proses logout..."); 
        fetch('/logout', { method: 'POST' }) 
            .then(() => {
                 // redirect ke halaman utama
                 window.location.href = '/'; 
            })
            .catch(error => {
                 console.error("Logout gagal:", error);
                 // tetap redirect meskipun fetch gagal
                 window.location.href = '/'; 
            });
    };

    const logoutButton = document.getElementById('logoutButton'); 
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    } 
    
});