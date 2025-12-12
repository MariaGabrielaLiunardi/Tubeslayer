// Edit Rubrik Dosen - JavaScript Handler

let komponenCounter = 1;

document.addEventListener('DOMContentLoaded', function() {
    // Logout handler
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', function() {
            if (confirm('Apakah Anda yakin ingin logout?')) {
                window.location.href = '/logout';
            }
        });
    }

    // Toggle sidebar
    const toggle = document.querySelector('.toggle');
    const sidebar = document.querySelector('.sidebar');
    
    if (toggle) {
        toggle.addEventListener('click', function() {
            sidebar.classList.toggle('close');
        });
    }

    // Form submit handler
    const rubrikForm = document.getElementById('rubrikForm');
    if (rubrikForm) {
        rubrikForm.addEventListener('submit', function(e) {
            const totalBobot = calculateTotalBobot();
            console.log("Form submit triggered. Total bobot: " + totalBobot);
            if (totalBobot !== 100) {
                e.preventDefault();
                alert('Total bobot harus 100%. Saat ini total bobot: ' + totalBobot + '%');
                return false;
            }
            console.log("Form will submit now");
        });
    }

    // Initial total bobot calculation
    updateTotalBobot();
    
    // Setup auto-resize untuk semua textareas
    setupTextareaAutoResize();
    
    // Initialize komponenCounter based on existing rows
    const container = document.getElementById('komponenContainer');
    const existingRows = container.querySelectorAll('.rubrik-row');
    komponenCounter = existingRows.length;
});

function setupTextareaAutoResize() {
    const textareas = document.querySelectorAll('.input-keterangan');
    textareas.forEach(textarea => {
        textarea.addEventListener('input', function() {
            autoResizeTextarea(this);
        });
        // Initial resize
        autoResizeTextarea(textarea);
    });
}

function autoResizeTextarea(textarea) {
    textarea.style.height = 'auto';
    textarea.style.height = Math.min(textarea.scrollHeight, 150) + 'px';
}

function tambahKomponen() {
    const container = document.getElementById('komponenContainer');
    const newIndex = container.children.length;
    
    const newRow = document.createElement('div');
    newRow.className = 'rubrik-row';
    newRow.setAttribute('data-index', newIndex);
    newRow.innerHTML = `
        <div class="col-isi-komponen">
            <input type="text" class="input-komponen" name="komponenPenilaian" placeholder="Masukkan nama komponen" required>
        </div>
        <div class="col-isi-bobot">
            <input type="number" class="input-bobot" name="bobot" min="0" max="100" placeholder="0" required>
        </div>
        <div class="col-isi-keterangan">
            <textarea class="input-keterangan" name="keterangan" placeholder="Masukkan keterangan (opsional)" rows="1"></textarea>
        </div>
        <div class="col-isi-aksi">
            <button type="button" class="btn-hapus-komponen" onclick="hapusKomponen(${newIndex})" title="Hapus Komponen">
                <i class='bx bx-trash'></i>
            </button>
        </div>
    `;
    
    container.appendChild(newRow);
    
    // Add event listener untuk bobot input
    const bobotInput = newRow.querySelector('.input-bobot');
    if (bobotInput) {
        bobotInput.addEventListener('input', updateTotalBobot);
    }
    
    // Setup auto-resize untuk textarea baru
    const newTextarea = newRow.querySelector('.input-keterangan');
    if (newTextarea) {
        newTextarea.addEventListener('input', function() {
            autoResizeTextarea(this);
        });
    }
    
    komponenCounter++;
}

function hapusKomponen(index) {
    const container = document.getElementById('komponenContainer');
    const rows = container.querySelectorAll('.rubrik-row');
    
    // Pastikan ada minimal 1 komponen
    if (rows.length <= 1) {
        alert('Minimal harus ada 1 komponen penilaian');
        return;
    }
    
    const rowToDelete = document.querySelector(`.rubrik-row[data-index="${index}"]`);
    if (rowToDelete) {
        rowToDelete.remove();
        updateTotalBobot();
    }
}

function calculateTotalBobot() {
    const bobotInputs = document.querySelectorAll('.input-bobot');
    let total = 0;
    
    bobotInputs.forEach(input => {
        const value = parseFloat(input.value) || 0;
        total += value;
    });
    
    return total;
}

function updateTotalBobot() {
    const totalBobot = calculateTotalBobot();
    const totalDisplay = document.getElementById('totalBobotDisplay');
    const bobotStatus = document.getElementById('bobotStatus');
    const btnSimpan = document.getElementById('btnSimpan');
    
    if (totalDisplay) {
        totalDisplay.textContent = totalBobot + '%';
    }
    
    if (bobotStatus) {
        bobotStatus.textContent = '';
        bobotStatus.className = 'bobot-status';
        
        if (totalBobot < 100) {
            bobotStatus.textContent = 'Total bobot belum mencapai 100% (Kurang ' + (100 - totalBobot) + '%)';
            bobotStatus.classList.add('invalid');
            if (btnSimpan) btnSimpan.disabled = true;
        } else if (totalBobot > 100) {
            bobotStatus.textContent = 'Total bobot melebihi 100% (Lebih ' + (totalBobot - 100) + '%)';
            bobotStatus.classList.add('invalid');
            if (btnSimpan) btnSimpan.disabled = true;
        } else {
            bobotStatus.textContent = '✓ Total bobot sudah tepat 100%';
            bobotStatus.classList.add('valid');
            if (btnSimpan) btnSimpan.disabled = false;
        }
    }
}

// Add event listeners to existing bobot inputs
document.addEventListener('input', function(e) {
    if (e.target.classList.contains('input-bobot')) {
        updateTotalBobot();
    }
});
