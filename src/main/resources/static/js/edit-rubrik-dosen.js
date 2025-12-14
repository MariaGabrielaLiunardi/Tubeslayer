
let komponenCounter = 1;

document.addEventListener('DOMContentLoaded', function() {
    console.log("=== EDIT RUBRIK PAGE LOADED ===");
    
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', function() {
            if (confirm('Apakah Anda yakin ingin logout?')) {
                window.location.href = '/logout';
            }
        });
    }

    const toggle = document.querySelector('.toggle');
    const sidebar = document.querySelector('.sidebar');
    
    if (toggle) {
        toggle.addEventListener('click', function() {
            sidebar.classList.toggle('close');
        });
    }
    
    const container = document.getElementById('komponenContainer');
    const existingRows = container.querySelectorAll('.rubrik-row');
    komponenCounter = existingRows.length;
    console.log("Existing rows: " + komponenCounter);
    
    if (existingRows.length === 0) {
        console.log("No komponen found, adding first one");
        tambahKomponen();
    }
    
    const bobotInputs = document.querySelectorAll('.input-bobot');
    bobotInputs.forEach(input => {
        input.addEventListener('input', updateTotalBobot);
    });
    
    const rubrikForm = document.getElementById('rubrikForm');
    if (rubrikForm) {
        console.log("Form found, adding submit handler");
        rubrikForm.addEventListener('submit', function(e) {
            console.log("========== FORM SUBMIT EVENT FIRED ==========");
            
            const container = document.getElementById('komponenContainer');
            const allRows = container.querySelectorAll('.rubrik-row');
            const rowsToKeep = [];
            const rowsToRemove = [];
            
            allRows.forEach((row, idx) => {
                const namaInput = row.querySelector('.input-komponen');
                const bobotInput = row.querySelector('.input-bobot');
                const namaValue = namaInput ? (namaInput.value ? namaInput.value.trim() : '') : '';
                const bobotValue = bobotInput ? (bobotInput.value ? parseInt(bobotInput.value) : 0) : 0;
                
                console.log("Row " + idx + ": nama='" + namaValue + "', bobot=" + bobotValue);
                
                if (namaValue !== '' && bobotValue > 0) {
                    rowsToKeep.push(row);
                } else {
                    rowsToRemove.push(row);
                }
            });
            
            console.log("Rows to keep: " + rowsToKeep.length + ", Rows to remove: " + rowsToRemove.length);
            
            rowsToRemove.forEach(row => {
                row.remove();
            });
            
            if (rowsToKeep.length === 0) {
                console.error("No valid komponen rows!");
                e.preventDefault();
                alert('Minimal harus ada 1 komponen penilaian dengan nama dan bobot');
                return false;
            }
            
            const totalBobot = calculateTotalBobot();
            console.log("Total bobot calculated: " + totalBobot);
            
            console.log("=== Form Data Being Submitted ===");
            const formData = new FormData(rubrikForm);
            let dataCount = 0;
            for (let [key, value] of formData.entries()) {
                console.log(key + ": " + value);
                dataCount++;
            }
            console.log("Total form fields: " + dataCount);
            console.log("==== End Form Data ====");
            
            if (totalBobot !== 100) {
                console.error("VALIDATION FAILED: Total bobot is " + totalBobot + ", expected 100");
                e.preventDefault();
                alert('❌ Total bobot harus 100%\n\nSaat ini: ' + totalBobot + '%\n\nSilakan atur bobot komponen sehingga totalnya tepat 100%');
                return false;
            }
            
            console.log("✅ VALIDATION OK - Total bobot = 100%, allowing form submission");
        });
    } else {
        console.error("Form #rubrikForm not found!");
    }

    updateTotalBobot();
    
    setupTextareaAutoResize();
    
    console.log("=== PAGE INITIALIZATION COMPLETE ===");
});

function setupTextareaAutoResize() {
    const textareas = document.querySelectorAll('.input-keterangan');
    textareas.forEach(textarea => {
        textarea.addEventListener('input', function() {
            autoResizeTextarea(this);
        });

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
    
    const bobotInput = newRow.querySelector('.input-bobot');
    if (bobotInput) {
        bobotInput.addEventListener('input', updateTotalBobot);
    }
    
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
    
    const validRows = Array.from(rows).filter(row => {
        const namaInput = row.querySelector('.input-komponen');
        const bobotInput = row.querySelector('.input-bobot');
        const namaValue = namaInput ? (namaInput.value ? namaInput.value.trim() : '') : '';
        const bobotValue = bobotInput ? (bobotInput.value ? parseInt(bobotInput.value) : 0) : 0;
        return namaValue !== '' && bobotValue > 0;
    });
    
    if (validRows.length <= 1) {
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
    
    console.log("🔍 updateTotalBobot: total=" + totalBobot + "%, button disabled=" + (btnSimpan ? btnSimpan.disabled : 'N/A'));
    
    if (totalDisplay) {
        totalDisplay.textContent = totalBobot + '%';
    }
    
    if (bobotStatus) {
        bobotStatus.textContent = '';
        bobotStatus.className = 'bobot-status';
        
        if (totalBobot < 100) {
            bobotStatus.textContent = '❌ Total bobot belum mencapai 100% (Kurang ' + (100 - totalBobot) + '%)';
            bobotStatus.classList.add('invalid');
            if (btnSimpan) {
                btnSimpan.disabled = true;
                console.warn("⚠️ Button DISABLED - Bobot hanya " + totalBobot + "%, kurang " + (100 - totalBobot) + "%");
            }
        } else if (totalBobot > 100) {
            bobotStatus.textContent = '❌ Total bobot melebihi 100% (Lebih ' + (totalBobot - 100) + '%)';
            bobotStatus.classList.add('invalid');
            if (btnSimpan) {
                btnSimpan.disabled = true;
                console.warn("⚠️ Button DISABLED - Bobot " + totalBobot + "%, lebih " + (totalBobot - 100) + "%");
            }
        } else {
            bobotStatus.textContent = '✅ Total bobot sudah tepat 100%';
            bobotStatus.classList.add('valid');
            if (btnSimpan) {
                btnSimpan.disabled = false;
                console.log("✅ Button ENABLED - Bobot = 100%");
            }
        }
    }
}

document.addEventListener('input', function(e) {
    if (e.target.classList.contains('input-bobot')) {
        const value = e.target.value;
        console.log("📝 Bobot input changed: " + value);
        updateTotalBobot();
    }
});