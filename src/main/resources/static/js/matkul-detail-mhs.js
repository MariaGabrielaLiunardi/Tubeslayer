document.addEventListener("DOMContentLoaded", () => {
    
    const mkTabDiv = document.querySelector('.mk-tab');
    const mataKuliahId = mkTabDiv ? mkTabDiv.getAttribute('data-mk-kode') : null;
    
    const tabButtons = document.querySelectorAll('.mk-tab .tab');

    tabButtons.forEach(button => {
        if (button.hasAttribute('data-target-url')) {
            button.addEventListener('click', function() {
                const url = this.getAttribute('data-target-url');
                if (url) {
                    window.location.href = url;
                }
            });
        }
    });

    // Logout
    
        const handleLogout = () => {
        console.log("Melakukan proses logout..."); 
        fetch('/logout', { method: 'POST' }) 
            .then(() => {
                 // redirect kembali ke halaman utama
                 window.location.href = '/'; 
            })
            .catch(error => {
                 console.error("Logout gagal:", error);
                 // tetap redirect kalau fetch gagal
                 window.location.href = '/'; 
            });
    };

    const logoutButton = document.getElementById('logoutButton'); 
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    } 
    
});