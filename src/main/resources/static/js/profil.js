// const user = JSON.parse(localStorage.getItem("currentUser"));

// if (!user) {
//     window.location.href = "/login";
// }

// document.getElementById("profil-nama").textContent = user.nama;
// document.getElementById("profil-email").textContent = "Email: " + user.email;
// document.getElementById("profil-role").textContent = user.role;

// if (user.role === "Mahasiswa") {
//     document.getElementById("profil-id").textContent = "NPM: " + user.id_user;
//     document.getElementById("profil-extra").textContent = "Status: Aktif";
// }

// if (user.role === "Dosen") {
//     document.getElementById("profil-id").textContent = "NIP: " + user.id_user;
//     document.getElementById("profil-extra").textContent = "Mengajar beberapa mata kuliah";
// }

// if (user.role === "Admin") {
//     document.getElementById("profil-id").textContent = "ID Admin: " + user.id_user;
//     document.getElementById("profil-extra").textContent = "Akses penuh sistem";
// }

// document.querySelector(".logout").addEventListener("click", () => {
//     localStorage.removeItem("currentUser");
//     window.location.href = "/login";
// });

const role = document.getElementById("profil-role").textContent.trim();

document.getElementById("sidebar-admin").style.display = 
    role === "Admin" ? "block" : "none";

document.getElementById("sidebar-dosen").style.display = 
    role === "Dosen" ? "block" : "none";

document.getElementById("sidebar-mahasiswa").style.display = 
    role === "Mahasiswa" ? "block" : "none";

