document.addEventListener("DOMContentLoaded", () => {
    const cards = document.querySelectorAll(".card");
    
    cards.forEach(card => {
        card.addEventListener("click", () => {
            // Ambil kode MK dari atribut data
            const kodeMk = card.getAttribute("data-kode-mk");
            
            if (kodeMk) {
                // KIRIMKAN SEBAGAI PARAMETER 'kodeMk'
                window.location.href = "/dosen/matkul-detail?kodeMk=" + encodeURIComponent(kodeMk);
            } else {
                 console.error("Error: Kode Mata Kuliah (data-kode-mk) tidak ditemukan pada kartu.");
            }
        });
    });

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