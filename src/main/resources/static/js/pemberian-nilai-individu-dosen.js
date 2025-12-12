document.addEventListener('DOMContentLoaded', () => {
    // 1. Logic untuk Toggle Sidebar

    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle');

    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }
});