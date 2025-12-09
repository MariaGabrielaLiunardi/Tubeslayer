// --- Fungsi Logout ---
function handleLogout() {
    console.log("Melakukan proses logout..."); 
    // Redirect ke halaman index, path disesuaikan jika perlu
    window.location.href = '/'; // Menggunakan path absolut untuk keamanan
}

document.addEventListener("DOMContentLoaded", () => {
    // 1. Logic Klik Kartu Mata Kuliah
    const cards = document.querySelectorAll(".card");
    
    cards.forEach(card => {
        card.addEventListener("click", () => {
            // Memastikan h3 ada di dalam card
            const titleElement = card.querySelector("h3");
            if (titleElement) {
                const title = titleElement.innerText;
                // Pindah ke halaman detail (matkul-detail.html)
                window.location.href = "/mahasiswa/matkul-detail?mk=" + encodeURIComponent(title);
            }
        });
    });
    
    // 2. Logic Tombol Logout
    const logoutButton = document.getElementById('logoutButton');
    
    if (logoutButton) {
        // Tombol Logout sekarang akan berfungsi
        logoutButton.addEventListener('click', handleLogout);
    } else {
        console.error("Error: Tombol Logout (ID: logoutButton) tidak ditemukan.");
    }
});