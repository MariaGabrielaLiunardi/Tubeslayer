document.addEventListener('DOMContentLoaded', () => {

    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.toggle');
    if (sidebar && toggle) {
        toggle.addEventListener('click', () => {
            sidebar.classList.toggle('close');
        });
    }

    const tambahKomponenButton = document.querySelector('.tambah-komponen-penilainan');
    const assignmentTableWrapper = document.querySelector('.assignment-table-wrapper');
    const totalBobotDisplay = document.querySelector('.total-bobot .bobot');
    const btnSimpan = document.querySelector('.btn-simpan');
    const btnBatal = document.querySelector('.btn-batal');
    const titleRubrik = document.querySelector('.title-rubrik');

    let savedRubricState = [];
    
    const existingRows = assignmentTableWrapper.querySelectorAll('.assignment-row');
    existingRows.forEach(row => row.remove());
    
    function captureCurrentState() {
        const rows = assignmentTableWrapper.querySelectorAll('.assignment-row');
        const state = [];
        rows.forEach(row => {
            const inputKomponen = row.querySelector('.col-isi-komponen input');
            const inputBobot = row.querySelector('.col-isi-bobot input');
            const inputKeterangan = row.querySelector('.col-isi-keterangan input');
            
            const komponen = inputKomponen ? inputKomponen.value : '';
            const bobot = inputBobot ? inputBobot.value : '';
            const keterangan = inputKeterangan ? inputKeterangan.value : '';
            
            state.push({ komponen, bobot, keterangan });
        });
        return state;
    }

    function deleteRow(rowElement) {
        if (rowElement && rowElement.parentNode) {
            rowElement.remove();
            updateBobotTotal();
        }
    }

    function updateBobotTotal() {
        const bobotInputs = assignmentTableWrapper.querySelectorAll('.col-isi-bobot input[type="number"]');
        let total = 0;
        
        bobotInputs.forEach(input => {
            const value = parseFloat(input.value) || 0;
            total += value;
        });

        totalBobotDisplay.textContent = `${total}%`;
        
        if (total !== 100) {
            totalBobotDisplay.style.color = 'red';
            return false;
        } else {
            totalBobotDisplay.style.color = 'green';
            return true;
        }
    }

    function createNewRubricRow() {
        const row = document.createElement('div');
        row.className = 'assignment-row';

        const colKomponen = document.createElement('div');
        colKomponen.className = 'col-isi-komponen';
        const inputKomponen = document.createElement('input');
        inputKomponen.type = 'text';
        inputKomponen.placeholder = 'Masukkan Komponen Penilaian';
        inputKomponen.style.cssText = 'width: 100%; border: none; outline: none; font-size: 18px;';
        colKomponen.appendChild(inputKomponen);

        const colBobot = document.createElement('div');
        colBobot.className = 'col-isi-bobot';
        const inputBobot = document.createElement('input');
        inputBobot.type = 'number';
        inputBobot.min = '0';
        inputBobot.max = '100';
        inputBobot.placeholder = '0';
        inputBobot.style.cssText = 'width: 100%; text-align: center; border: none; outline: none; font-size: 18px;';
        inputBobot.addEventListener('input', updateBobotTotal);
        colBobot.appendChild(inputBobot);

        const colKeterangan = document.createElement('div');
        colKeterangan.className = 'col-isi-keterangan';
        const inputKeterangan = document.createElement('input');
        inputKeterangan.type = 'text';
        inputKeterangan.placeholder = 'Opsional';
        inputKeterangan.style.cssText = 'width: 100%; border: none; outline: none; font-size: 18px;';
        colKeterangan.appendChild(inputKeterangan);
        
        const colDelete = document.createElement('div');
        colDelete.className = 'col-delete';
        const deleteButton = document.createElement('button');
        deleteButton.className = 'delete-btn';
        deleteButton.innerHTML = '<i class="bx bx-trash"></i>'; 
        
        colDelete.appendChild(deleteButton);
        
        row.appendChild(colKomponen);
        row.appendChild(colBobot);
        row.appendChild(colKeterangan);
        row.appendChild(colDelete); 

        return row;
    }

    assignmentTableWrapper.addEventListener('click', (event) => {
        const target = event.target;

        const deleteButton = target.closest('.delete-btn');
        
        if (deleteButton) {

            const rowToDelete = deleteButton.closest('.assignment-row');
            if (rowToDelete) {
                deleteRow(rowToDelete);
            }
        }
    });

    if (tambahKomponenButton && assignmentTableWrapper) {
        tambahKomponenButton.addEventListener('click', () => {
            const newRow = createNewRubricRow();
            const tambahKomponenDiv = assignmentTableWrapper.querySelector('.tambah-komponen');
            
            if (tambahKomponenDiv) {
                assignmentTableWrapper.insertBefore(newRow, tambahKomponenDiv);
            } else {
                assignmentTableWrapper.appendChild(newRow);
            }
            updateBobotTotal();
        });
    }

    if (btnSimpan) {
        btnSimpan.addEventListener('click', () => {
            if (updateBobotTotal()) {

                savedRubricState = captureCurrentState();
                
                alert('Rubrik berhasil disimpan!');
                
            } else {
                alert('Gagal menyimpan. Total Bobot harus 100%.');
            }
        });
    }
    
    if (btnBatal) {
        btnBatal.addEventListener('click', () => {

            if (confirm('Apakah Anda yakin ingin membatalkan perubahan? Perubahan yang belum disimpan akan hilang.')) {

                 window.history.back(); 
            }
        });
    }
    
    if (titleRubrik) {

        const penilainanLink = titleRubrik.querySelector('a');
        const fullText = titleRubrik.textContent;
        const parts = fullText.split('>');
        
        if (penilainanLink) {

            penilainanLink.style.color = '#2269a3'; 
            penilainanLink.style.textDecoration = 'none';
        }
    }

    savedRubricState = captureCurrentState();
    updateBobotTotal();
});