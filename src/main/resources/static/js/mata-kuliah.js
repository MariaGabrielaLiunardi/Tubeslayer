document.addEventListener("DOMContentLoaded", () => {
  const cards = document.querySelectorAll(".card");
  const courseGrid = document.querySelector(".course-grid");
  const footer = document.querySelector(".semester");
  const mainContent = document.querySelector(".main-content");

  cards.forEach(card => {
    card.addEventListener("click", () => {
      const title = card.querySelector("h3").innerText;

      // ✅ Untuk sementara tampilkan alert dulu
      // (karena belum ada halaman detail MK di HTML)
      alert("Mata kuliah dipilih: " + title);

      // ✅ Kalau nanti mau diarahkan ke halaman detail:
      // window.location.href = "detail-mata-kuliah.html?mk=" + encodeURIComponent(title);
    });
  });
});
