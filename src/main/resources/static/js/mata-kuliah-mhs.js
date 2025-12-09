document.addEventListener("DOMContentLoaded", () => {
    // 1. Logic Klik Kartu Mata Kuliah
    const cards = document.querySelectorAll(".card");
    
    cards.forEach(card => {
        card.addEventListener("click", () => {
            // Ambil kode MK dari atribut data
            const kodeMk = card.getAttribute("data-kode-mk"); 
            
            if (kodeMk) {
                // KIRIMKAN SEBAGAI PARAMETER 'mk' (sesuai MahasiswaController)
                // MahasiswaController menggunakan @RequestParam("mk")
                window.location.href = "/mahasiswa/matkul-detail?mk=" + encodeURIComponent(kodeMk);
            } else {
                 console.error("Error: Kode Mata Kuliah (data-kode-mk) tidak ditemukan pada kartu.");
            }
        });
    });
    
    // 2. Logic Tombol Logout (Diambil dari dash-user.js atau diulang di sini)
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