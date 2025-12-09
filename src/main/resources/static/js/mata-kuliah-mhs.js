document.addEventListener("DOMContentLoaded", () => {
    const cards = document.querySelectorAll(".card");
    
    cards.forEach(card => {
        card.addEventListener("click", () => {
            // Ambil kode MK dari atribut data
            const kodeMk = card.getAttribute("data-kode-mk"); 
            
            if (kodeMk) {
                window.location.href = "/mahasiswa/matkul-detail?mk=" + encodeURIComponent(kodeMk);
            } else {
                 console.error("Error: Kode Mata Kuliah (data-kode-mk).");
            }
        });
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