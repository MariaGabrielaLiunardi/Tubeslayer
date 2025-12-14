
function getCurrentUser() {

    try {
        const userData = localStorage.getItem("currentUser");
        if (userData) {
            return JSON.parse(userData);
        }
    } catch (error) {
        console.warn("Error parsing localStorage currentUser:", error);

    }

    const nameEl = document.getElementById('profil-nama');
    const emailEl = document.getElementById('profil-email');
    const roleEl = document.getElementById('profil-role');

    if (nameEl || emailEl || roleEl) {
        const user = {
            nama: nameEl ? nameEl.textContent.trim() : null,
            email: emailEl ? emailEl.textContent.replace(/^Email:\s*/i, '').trim() : null,
            role: roleEl ? roleEl.textContent.trim() : null
        };

        if (user.nama || user.email || user.role) {
            return user;
        }
    }

    window.location.href = "/login";
    return null;
}

function setupRoleBasedUI(user) {
    if (!user) return;

    const nameEl = document.getElementById("profil-nama");
    const emailEl = document.getElementById("profil-email");
    const roleEl = document.getElementById("profil-role");

    if (nameEl) nameEl.textContent = user.nama || "User";
    if (roleEl) roleEl.textContent = user.role || "User";

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

    setupSidebar(user.role);
    setupMenuItems(user.role);
}

function setupSidebar(role) {

    const sidebars = ["sidebar-admin", "sidebar-dosen", "sidebar-mahasiswa"];
    sidebars.forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.style.display = "none";
        }
    });

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

function setupMenuItems(role) {

    const adminOnlyElements = document.querySelectorAll('.admin-only');
    const dosenOnlyElements = document.querySelectorAll('.dosen-only');
    const mahasiswaOnlyElements = document.querySelectorAll('.mahasiswa-only');

    adminOnlyElements.forEach(el => el.style.display = 'none');
    dosenOnlyElements.forEach(el => el.style.display = 'none');
    mahasiswaOnlyElements.forEach(el => el.style.display = 'none');

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

function hasRole(requiredRole) {
    const user = getCurrentUser();
    return user && user.role === requiredRole;
}

function checkPageAccess(allowedRoles) {
    const user = getCurrentUser();
    if (!user || !allowedRoles.includes(user.role)) {

        window.location.href = "/unauthorized";
        return false;
    }
    return true;
}

function initializeUserSystem() {

    const user = getCurrentUser();
    if (!user) return;

    setupRoleBasedUI(user);

    const logoutButtons = document.querySelectorAll(".logout");
    logoutButtons.forEach(button => {
        button.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    });

    const navLogoutBtn = document.querySelector("button.logout");
    if (navLogoutBtn) {
        navLogoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    }

    console.log("Current user:", user);
}

function logout() {
    try {
        localStorage.removeItem('currentUser');
    } catch (e) {
        console.warn('Error clearing localStorage during logout', e);
    }

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

    window.location.href = '/logout';

    setTimeout(() => { window.location.href = '/login'; }, 1500);
}

function simpleRoleCheck() {
    const userData = localStorage.getItem("currentUser");
    
    if (!userData) {
        window.location.href = "/login";
        return null;
    }

    const user = JSON.parse(userData);
    
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

    ['sidebar-admin', 'sidebar-dosen', 'sidebar-mahasiswa'].forEach(id => {
        const element = document.getElementById(id);
        if (element) element.style.display = 'none';
    });
    const roleElementId = 'sidebar-' + (user.role || '').toLowerCase();
    const roleElement = document.getElementById(roleElementId);
    if (roleElement) roleElement.style.display = 'block';

    return user;
}

document.addEventListener('DOMContentLoaded', initializeUserSystem);

if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        getCurrentUser,
        hasRole,
        checkPageAccess,
        logout
    };
}