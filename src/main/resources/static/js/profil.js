// Fungsi untuk mengambil user dari localStorage
function getCurrentUser() {
    // 1) Try to get user from localStorage (if login flow stores it)
    try {
        const userData = localStorage.getItem("currentUser");
        if (userData) {
            return JSON.parse(userData);
        }
    } catch (error) {
        console.warn("Error parsing localStorage currentUser:", error);
        // continue to try DOM-injected values
    }

    // 2) Fall back to server-injected DOM values (Thymeleaf sets these in the template)
    const nameEl = document.getElementById('profil-nama');
    const emailEl = document.getElementById('profil-email');
    const roleEl = document.getElementById('profil-role');

    if (nameEl || emailEl || roleEl) {
        const user = {
            nama: nameEl ? nameEl.textContent.trim() : null,
            email: emailEl ? emailEl.textContent.replace(/^Email:\s*/i, '').trim() : null,
            role: roleEl ? roleEl.textContent.trim() : null
        };

        // If any useful value exists, treat as authenticated user
        if (user.nama || user.email || user.role) {
            return user;
        }
    }

    // 3) No user found -> redirect to login
    window.location.href = "/login";
    return null;
}

// Fungsi untuk mengatur tampilan berdasarkan role
function setupRoleBasedUI(user) {
    if (!user) return;
    // Set profile information (only IDs present in profil.html)
    const nameEl = document.getElementById("profil-nama");
    const emailEl = document.getElementById("profil-email");
    const roleEl = document.getElementById("profil-role");

    if (nameEl) nameEl.textContent = user.nama || "User";
    if (roleEl) roleEl.textContent = user.role || "User";

    // Consolidate role-specific info into the email line (profil-email exists in HTML)
    if (emailEl) {
        const email = user.email || "N/A";
        let extra = "";
        switch (user.role) {
            case "Mahasiswa":
                extra = user.nim || user.id_user ? ` | NPM: ${user.nim || user.id_user}` : "";
                if (user.angkatan) extra += ` | Angkatan: ${user.angkatan}`;
                break;
            case "Dosen":
                extra = user.nip || user.id_user ? ` | NIP: ${user.nip || user.id_user}` : "";
                break;
            case "Admin":
                extra = user.id_user ? ` | ID: ${user.id_user}` : "";
                break;
        }
        emailEl.textContent = `Email: ${email}${extra}`;
    }

    // Setup sidebar visibility and menu items based on role
    setupSidebar(user.role);
    setupMenuItems(user.role);
}

// Fungsi untuk mengatur sidebar berdasarkan role
function setupSidebar(role) {
    // Hide all sidebars first
    const sidebars = ["sidebar-admin", "sidebar-dosen", "sidebar-mahasiswa"];
    sidebars.forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.style.display = "none";
        }
    });

    // Show specific sidebar based on role
    switch(role) {
        case "Admin":
            if (document.getElementById("sidebar-admin")) {
                document.getElementById("sidebar-admin").style.display = "block";
            }
            break;
        case "Dosen":
            if (document.getElementById("sidebar-dosen")) {
                document.getElementById("sidebar-dosen").style.display = "block";
            }
            break;
        case "Mahasiswa":
            if (document.getElementById("sidebar-mahasiswa")) {
                document.getElementById("sidebar-mahasiswa").style.display = "block";
            }
            break;
    }
}

// Fungsi untuk mengatur menu items berdasarkan role
function setupMenuItems(role) {
    // Contoh: Sembunyikan/tampilkan menu tertentu berdasarkan role
    const adminOnlyElements = document.querySelectorAll('.admin-only');
    const dosenOnlyElements = document.querySelectorAll('.dosen-only');
    const mahasiswaOnlyElements = document.querySelectorAll('.mahasiswa-only');

    // Hide all first
    adminOnlyElements.forEach(el => el.style.display = 'none');
    dosenOnlyElements.forEach(el => el.style.display = 'none');
    mahasiswaOnlyElements.forEach(el => el.style.display = 'none');

    // Show based on role
    switch(role) {
        case "Admin":
            adminOnlyElements.forEach(el => el.style.display = 'block');
            break;
        case "Dosen":
            dosenOnlyElements.forEach(el => el.style.display = 'block');
            break;
        case "Mahasiswa":
            mahasiswaOnlyElements.forEach(el => el.style.display = 'block');
            break;
    }
}

// Fungsi untuk memeriksa apakah user memiliki role tertentu
function hasRole(requiredRole) {
    const user = getCurrentUser();
    return user && user.role === requiredRole;
}

// Fungsi untuk memeriksa akses ke halaman
function checkPageAccess(allowedRoles) {
    const user = getCurrentUser();
    if (!user || !allowedRoles.includes(user.role)) {
        // Redirect to unauthorized page or dashboard
        window.location.href = "/unauthorized";
        return false;
    }
    return true;
}

// Fungsi utama yang dijalankan ketika halaman dimuat
function initializeUserSystem() {
    // Get current user
    const user = getCurrentUser();
    if (!user) return;

    // Setup UI based on user role
    setupRoleBasedUI(user);

    // Setup logout button
    const logoutButtons = document.querySelectorAll(".logout");
    logoutButtons.forEach(button => {
        button.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    });

    // Setup logout button di navbar jika ada
    const navLogoutBtn = document.querySelector("button.logout");
    if (navLogoutBtn) {
        navLogoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    }

    // Optional: Add user info to console for debugging
    console.log("Current user:", user);
}

// Logout helper: clear client storage and trigger server logout
function logout() {
    try {
        localStorage.removeItem('currentUser');
    } catch (e) {
        console.warn('Error clearing localStorage during logout', e);
    }

    // Try to perform a POST logout (works when CSRF not required or default Spring logout is enabled)
    try {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/logout';
        form.style.display = 'none';
        document.body.appendChild(form);
        form.submit();
        return;
    } catch (e) {
        console.warn('POST logout failed, falling back to GET', e);
    }

    // Fallback: navigate to logout URL (may be handled by server) then to login
    window.location.href = '/logout';
    // Safety: if server doesn't handle logout redirect, ensure user lands on login
    setTimeout(() => { window.location.href = '/login'; }, 1500);
}

// Versi alternatif yang lebih sederhana jika perlu
function simpleRoleCheck() {
    const userData = localStorage.getItem("currentUser");
    
    if (!userData) {
        window.location.href = "/login";
        return null;
    }

    const user = JSON.parse(userData);
    
    // Basic info display (populate only existing IDs)
    const nameEl = document.getElementById('profil-nama');
    const emailEl = document.getElementById('profil-email');
    const roleEl = document.getElementById('profil-role');

    if (nameEl) nameEl.textContent = user.nama || '';
    if (roleEl) roleEl.textContent = user.role || '';
    if (emailEl) {
        const email = user.email || 'N/A';
        let extra = '';
        switch(user.role) {
            case 'Mahasiswa':
                extra = user.nim || user.id_user ? ` | NPM: ${user.nim || user.id_user}` : '';
                if (user.angkatan) extra += ` | Angkatan: ${user.angkatan}`;
                break;
            case 'Dosen':
                extra = user.nip || user.id_user ? ` | NIP: ${user.nip || user.id_user}` : '';
                break;
            case 'Admin':
                extra = user.id_user ? ` | ID: ${user.id_user}` : '';
                break;
        }
        emailEl.textContent = `Email: ${email}${extra}`;
    }

    // Setup sidebar visibility
    ['sidebar-admin', 'sidebar-dosen', 'sidebar-mahasiswa'].forEach(id => {
        const element = document.getElementById(id);
        if (element) element.style.display = 'none';
    });
    const roleElementId = 'sidebar-' + (user.role || '').toLowerCase();
    const roleElement = document.getElementById(roleElementId);
    if (roleElement) roleElement.style.display = 'block';

    return user;
}

// Pilih salah satu metode berdasarkan kebutuhan:

// Opsi 1: Menggunakan sistem lengkap (direkomendasikan)
document.addEventListener('DOMContentLoaded', initializeUserSystem);

// Opsi 2: Menggunakan versi sederhana
// document.addEventListener('DOMContentLoaded', simpleRoleCheck);

// Export fungsi jika menggunakan module system
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        getCurrentUser,
        hasRole,
        checkPageAccess,
        logout
    };
}