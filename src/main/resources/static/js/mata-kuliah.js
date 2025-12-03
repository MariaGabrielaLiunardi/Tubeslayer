document.addEventListener("DOMContentLoaded", () => {
    const cards = document.querySelectorAll(".card");
    
    cards.forEach(card => {
        card.addEventListener("click", () => {
            const title = card.querySelector("h3").innerText;
            
            // Pindah ke halaman detail (matkul-detail.html)
            window.location.href = "/mahasiswa/matkul-detail?mk=" + encodeURIComponent(title);
        });
    });
    
});