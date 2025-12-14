document.addEventListener("DOMContentLoaded", () => {
    const cards = document.querySelectorAll(".card");

    cards.forEach(card => {

        card.addEventListener("click", () => {
            const kodeMk = card.getAttribute("data-kode-mk");
            if (kodeMk) {
                window.location.href = "/dosen/matkul-detail?kodeMk=" + encodeURIComponent(kodeMk);
            } else {
                console.error("Error: Kode Mata Kuliah tidak ditemukan.");
            }
        });
    });

        const handleLogout = () => {
        console.log("Melakukan proses logout..."); 
        fetch('/logout', { method: 'POST' }) 
            .then(() => {

                 window.location.href = '/'; 
            })
            .catch(error => {
                 console.error("Logout gagal:", error);

                 window.location.href = '/'; 
            });
    };

    const logoutButton = document.getElementById('logoutButton'); 
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    } 
    
});