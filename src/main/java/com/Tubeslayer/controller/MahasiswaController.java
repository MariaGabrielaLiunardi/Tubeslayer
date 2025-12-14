package com.Tubeslayer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Tubeslayer.repository.MataKuliahRepository;
import com.Tubeslayer.repository.TugasBesarRepository;
import com.Tubeslayer.repository.NilaiRepository;
import com.Tubeslayer.repository.UserKelompokRepository;
import com.Tubeslayer.repository.KelompokRepository;
import com.Tubeslayer.repository.jdbc.KelompokJdbcRepository.AnggotaKelompokDTO;
import com.Tubeslayer.repository.MataKuliahMahasiswaRepository;
import com.Tubeslayer.repository.MataKuliahDosenRepository; 
import com.Tubeslayer.dto.PesertaMatkulDTO;

import java.util.List;
import java.util.Optional; 
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.Tubeslayer.entity.MataKuliah;
import com.Tubeslayer.entity.MataKuliahDosen; 
import com.Tubeslayer.entity.MataKuliahMahasiswa;
import com.Tubeslayer.entity.TugasBesar;
import com.Tubeslayer.entity.Nilai;
import com.Tubeslayer.entity.UserKelompok;
import com.Tubeslayer.entity.Kelompok;
import com.Tubeslayer.entity.RubrikNilai;
import com.Tubeslayer.entity.KomponenNilai;
import com.Tubeslayer.dto.MahasiswaSearchDTO;
import com.Tubeslayer.repository.jdbc.KelompokJdbcRepository.AnggotaKelompokDTO;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Tubeslayer.service.CustomUserDetails;
import com.Tubeslayer.service.DashboardMahasiswaService;
import com.Tubeslayer.service.KelompokJdbcService;
import com.Tubeslayer.service.MataKuliahService;
import com.Tubeslayer.entity.MataKuliah;

import java.util.List; 
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.Comparator; 

@Controller
public class MahasiswaController {

    private static final Logger logger = LoggerFactory.getLogger(MahasiswaController.class);

    @Autowired
    private MataKuliahRepository mataKuliahRepo;

    @Autowired
    private TugasBesarRepository tugasRepo;

    @Autowired
    private MataKuliahMahasiswaRepository mkmRepo;
    
    @Autowired 
    private MataKuliahDosenRepository mkDosenRepo;

    @Autowired
    private NilaiRepository nilaiRepository;

    @Autowired
    private UserKelompokRepository userKelompokRepo;

    @Autowired
    private KelompokRepository kelompokRepo;

    @Autowired
    private KelompokJdbcService kelompokJdbcService;

    @Autowired
    private com.Tubeslayer.repository.KomponenNilaiRepository komponenNilaiRepo;

    private final DashboardMahasiswaService dashboardService;
    private final MataKuliahService mataKuliahService;

    public MahasiswaController(DashboardMahasiswaService dashboardService,
                               MataKuliahService mataKuliahService) {
        this.dashboardService = dashboardService;
        this.mataKuliahService = mataKuliahService;
    }

    @GetMapping("/mahasiswa/dashboard")
    public String mahasiswaDashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        System.out.println("Controller mahasiswaDashboard dipanggil!");
        model.addAttribute("user", user);

        LocalDate today = LocalDate.now();
        model.addAttribute("tanggal", today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

        int year = today.getYear();
        String semesterTahunAjaran = (today.getMonthValue() >= 7) ? year + "/" + (year + 1) : (year - 1) + "/" + year;
        String semesterLabel = (today.getMonthValue() >= 9 || today.getMonthValue() <= 2) ? "Ganjil" : "Genap";
        
        model.addAttribute("semesterTahunAjaran", semesterTahunAjaran);
        model.addAttribute("semesterLabel", semesterLabel);

        String tahunAkademik = (today.getMonthValue() >= 7) ? year + "/" + (year + 1) : (year - 1) + "/" + year;
        model.addAttribute("semester", tahunAkademik);

        int jumlahMk = dashboardService.getJumlahMkAktif(user.getIdUser(), tahunAkademik);
        int jumlahTb = dashboardService.getJumlahTbAktif(user.getIdUser());
        model.addAttribute("jumlahMk", jumlahMk);
        model.addAttribute("jumlahTb", jumlahTb);

        List<MataKuliahMahasiswa> enrollList = mkmRepo.findByUser_IdUserAndIsActive(user.getIdUser(), true);

        List<MataKuliahMahasiswa> filteredEnrollList = enrollList.stream()
            .filter(e -> {

                 return e.getMataKuliah() != null; 

            })
            .collect(Collectors.toList());

        int gradientCount = 4;
        for (MataKuliahMahasiswa enroll : filteredEnrollList) {
            String kodeMK = enroll.getMataKuliah().getKodeMK();

            int colorIndex = Math.abs(kodeMK.hashCode()) % gradientCount;
            enroll.setColorIndex(colorIndex);
        }

        filteredEnrollList.sort(Comparator.comparing(mk -> mk.getMataKuliah().getNama()));

        List<MataKuliahMahasiswa> limitedEnrollList = filteredEnrollList.stream()
            .limit(4)
            .collect(Collectors.toList());

        model.addAttribute("enrollList", limitedEnrollList); 

        return "mahasiswa/dashboard";
    }

    @GetMapping("/mahasiswa/mata-kuliah")
    public String mataKuliah(@AuthenticationPrincipal CustomUserDetails user, Model model) {

        String idMahasiswa = user.getIdUser();  

        List<MataKuliahMahasiswa> enrollList = mkmRepo.findByUser_IdUserAndIsActive(idMahasiswa, true);

        int gradientCount = 4;
        enrollList.forEach(enroll -> {

            String kodeMK = enroll.getMataKuliah().getKodeMK();
            int colorIndex = Math.abs(kodeMK.hashCode()) % gradientCount;
            enroll.setColorIndex(colorIndex);
        });

        enrollList.sort(Comparator.comparing(mk -> mk.getMataKuliah().getNama()));

        model.addAttribute("enrollList", enrollList);
        model.addAttribute("user", user);

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        String semesterTahunAjaran; 
        String semesterLabel; 

        semesterTahunAjaran = (today.getMonthValue() >= 7) ?
                year + "/" + (year + 1) :
                (year - 1) + "/" + year;

        if (today.getMonthValue() >= 9 || today.getMonthValue() <= 2) { 
            semesterLabel = "Ganjil"; 
        } else {
            semesterLabel = "Genap";
        }

        model.addAttribute("semesterTahunAjaran", semesterTahunAjaran);
        model.addAttribute("semesterLabel", semesterLabel);

        return "mahasiswa/mata-kuliah";
    }

    @GetMapping("/mahasiswa/matkul-detail")
    public String detailMatkul(
            @RequestParam("mk") String kodeMk,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {

        if (kodeMk == null || kodeMk.isEmpty()) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        MataKuliah mkDetail = mataKuliahRepo.findById(kodeMk).orElse(null);
        if (mkDetail == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        int gradientCount = 4;
        int colorIndex = Math.abs(kodeMk.hashCode()) % gradientCount;
        model.addAttribute("colorIndex", colorIndex);

        MataKuliahDosen koordinator = null;
        List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
        if (!dosenList.isEmpty()) {
            koordinator = dosenList.get(0);
        }
        model.addAttribute("koordinator", koordinator);

        List<TugasBesar> tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
        model.addAttribute("mkDetail", mkDetail);
        model.addAttribute("tugasList", tugasList);
        model.addAttribute("user", user);

        return "mahasiswa/matkul-detail";
    }

    @GetMapping("/mahasiswa/matkul-peserta")
    public String peserta(@RequestParam(required = false) String kodeMk, 
                    @RequestParam(required = false) Integer colorIndex,
                      @AuthenticationPrincipal CustomUserDetails user, 
                      Model model) {
     
        if (kodeMk == null || kodeMk.isEmpty()) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        MataKuliah mk = mataKuliahRepo.findById(kodeMk).orElse(null);

        if (mk == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
        model.addAttribute("colorIndex", finalColorIndex);

        MataKuliahDosen koordinatorDosen = null;
        try {
            List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(kodeMk, true);
                if (!dosenList.isEmpty()) {
                    koordinatorDosen = dosenList.get(0);
                }
            } catch (Exception e) {
                System.err.println("Error fetching coordinator for peserta: " + e.getMessage());
            }
        model.addAttribute("koordinator", koordinatorDosen); 

        List<MataKuliahMahasiswa> listPeserta = Collections.emptyList();
    
        if (mkmRepo != null) {
            try {
                listPeserta = mkmRepo.findByMataKuliah_KodeMKAndIsActive(mk.getKodeMK(), true); 
            } catch (Exception e) {
                System.err.println("Error saat mengambil data peserta: " + e.getMessage());
            }
        }
    
        List<PesertaMatkulDTO> combinedList = new ArrayList<>();
        int counter = 1;

        if (koordinatorDosen != null) {
            combinedList.add(new PesertaMatkulDTO(
                counter++, 
                koordinatorDosen.getUser().getNama(), 
                koordinatorDosen.getUser().getIdUser(), 
                "Koordinator"
            ));
        }
    
        List<PesertaMatkulDTO> mahasiswaDTOs = listPeserta.stream()
            .map(rel -> new PesertaMatkulDTO(
                0, 
                rel.getUser().getNama(),
                rel.getUser().getIdUser(),
                "Mahasiswa",
                rel.getKelas()
            ))
        .collect(Collectors.toList());
        
        mahasiswaDTOs.sort(Comparator.comparing(PesertaMatkulDTO::getNama));

        combinedList.addAll(mahasiswaDTOs);
        for (int i = 1; i < combinedList.size(); i++) {
            combinedList.get(i).setNo(counter++);
        }

        model.addAttribute("mkDetail", mk);
        model.addAttribute("user", user); 
        model.addAttribute("combinedPesertaList", combinedList);
        model.addAttribute("pesertaCount", listPeserta.size());

        return "mahasiswa/matkul-peserta";
    }

    @GetMapping("/mahasiswa/tugas-detail")
    public String tugasDetail(
            @RequestParam("idTugas") Integer idTugas,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {

        if (idTugas == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        Optional<TugasBesar> tugasOpt = tugasRepo.findById(idTugas);

        if (!tugasOpt.isPresent()) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        TugasBesar tugas = tugasOpt.get();
        MataKuliah mkDetail = tugas.getMataKuliah();

        MataKuliahDosen koordinator = null;
        try {
            List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(
                mkDetail.getKodeMK(), true);
            if (!dosenList.isEmpty()) {
                koordinator = dosenList.get(0);
            }
        } catch (Exception e) {
            System.err.println("Error fetching coordinator: " + e.getMessage());
        }

        String modeKelompok = kelompokJdbcService.getModeKelompok(idTugas);
        boolean hasKelompok = kelompokJdbcService.hasKelompok(idTugas, user.getIdUser());
        boolean isLeader = kelompokJdbcService.isLeader(idTugas, user.getIdUser());
        boolean canManage = kelompokJdbcService.canManageAnggota(idTugas, user.getIdUser());

        int jumlahAnggota = kelompokJdbcService.countAnggota(idTugas, user.getIdUser());
        int maxAnggota = kelompokJdbcService.getMaxAnggota(idTugas);
        String namaKelompok = kelompokJdbcService.getNamaKelompok(idTugas, user.getIdUser());

        List<AnggotaKelompokDTO> anggotaList = Collections.emptyList();
        if (hasKelompok) {
            try {
                anggotaList = kelompokJdbcService.getAnggotaKelompok(idTugas, user.getIdUser());
            } catch (Exception e) {
                System.err.println("Error fetching anggota: " + e.getMessage());
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("tugas", tugas);
        model.addAttribute("mkDetail", mkDetail);
        model.addAttribute("koordinator", koordinator);

        model.addAttribute("modeKelompok", modeKelompok);
        model.addAttribute("hasKelompok", hasKelompok);
        model.addAttribute("isLeader", isLeader);
        model.addAttribute("canManageAnggota", canManage);

        model.addAttribute("jumlahAnggota", jumlahAnggota);
        model.addAttribute("maxAnggota", maxAnggota);
        
        model.addAttribute("namaKelompok", namaKelompok != null ? namaKelompok : "Belum ada kelompok");
        model.addAttribute("anggotaPreview", anggotaList);
        
        return "hlmn_tubes/hlmtubes";
    }

    @PostMapping("/mahasiswa/api/search-mahasiswa")
    @ResponseBody
    public ResponseEntity<?> searchMahasiswa(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal CustomUserDetails user) {

        try {

            if (!request.containsKey("idTugas") || !request.containsKey("keyword")) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("idTugas dan keyword harus diisi"));
            }

            Integer idTugas = Integer.parseInt(request.get("idTugas").toString());
            String keyword = request.get("keyword").toString().trim();

            if (keyword.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Keyword tidak boleh kosong"));
            }

            List<MahasiswaSearchDTO> results = 
                kelompokJdbcService.searchMahasiswa(idTugas, keyword);

            return ResponseEntity.ok(results);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Format idTugas tidak valid"));
        } catch (Exception e) {
            System.err.println("Error searching mahasiswa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Terjadi kesalahan saat mencari mahasiswa"));
        }
    }

    @GetMapping("/mahasiswa/api/anggota-kelompok")
    @ResponseBody
    public ResponseEntity<?> getAnggotaKelompok(
            @RequestParam("idTugas") Integer idTugas,
            @AuthenticationPrincipal CustomUserDetails user) {

        try {
            List<AnggotaKelompokDTO> anggotaList = 
                kelompokJdbcService.getAnggotaKelompok(idTugas, user.getIdUser());

            return ResponseEntity.ok(anggotaList);

        } catch (Exception e) {
            System.err.println("Error getting anggota kelompok: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Terjadi kesalahan saat mengambil data anggota"));
        }
    }

    @PostMapping("/mahasiswa/api/tambah-anggota")
    @ResponseBody
    public ResponseEntity<?> tambahAnggota(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal CustomUserDetails user) {

        try {

            if (!request.containsKey("idTugas") || !request.containsKey("idAnggota")) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("idTugas dan idAnggota harus diisi"));
            }

            Integer idTugas = Integer.parseInt(request.get("idTugas").toString());
            String idAnggota = request.get("idAnggota").toString();

            kelompokJdbcService.tambahAnggota(idTugas, user.getIdUser(), idAnggota);

            int jumlahAnggota = kelompokJdbcService.countAnggota(idTugas, user.getIdUser());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anggota berhasil ditambahkan");
            response.put("jumlahAnggota", jumlahAnggota);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error tambah anggota: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Terjadi kesalahan saat menambahkan anggota"));
        }
    }

    @PostMapping("/mahasiswa/api/hapus-anggota")
    @ResponseBody
    public ResponseEntity<?> hapusAnggota(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal CustomUserDetails user) {

        try {

            if (!request.containsKey("idTugas") || !request.containsKey("idAnggota")) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("idTugas dan idAnggota harus diisi"));
            }

            Integer idTugas = Integer.parseInt(request.get("idTugas").toString());
            String idAnggota = request.get("idAnggota").toString();

            kelompokJdbcService.hapusAnggota(idTugas, user.getIdUser(), idAnggota);

            int jumlahAnggota = kelompokJdbcService.countAnggota(idTugas, user.getIdUser());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anggota berhasil dihapus");
            response.put("jumlahAnggota", jumlahAnggota);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error hapus anggota: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Terjadi kesalahan saat menghapus anggota"));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    @GetMapping("/mahasiswa/nilai")
    public String mahasiswaNilai(
            @RequestParam(required = false) String mk,
            @RequestParam(required = false) Integer colorIndex,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        
        if (mk == null || mk.isEmpty()) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(mk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        List<MataKuliahMahasiswa> enrollments = mkmRepo.findByMataKuliah_KodeMKAndIsActive(mk, true);
        boolean isEnrolled = enrollments.stream()
            .anyMatch(e -> e.getUser().getIdUser().equals(user.getIdUser()));
        
        if (!isEnrolled) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        MataKuliahDosen koordinator = null;
        List<MataKuliahDosen> dosenList = mkDosenRepo.findByMataKuliah_KodeMKAndIsActive(mk, true);
        if (!dosenList.isEmpty()) {
            koordinator = dosenList.get(0);
        }

        List<TugasBesar> tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(mk, true);
        tugasList.sort(Comparator.comparing(TugasBesar::getDeadline));

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
        
        model.addAttribute("user", user);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("koordinator", koordinator);
        model.addAttribute("tugasList", tugasList);
        model.addAttribute("kodeMk", mk);
        model.addAttribute("colorIndex", finalColorIndex);

        return "nilai/Mahasiswa/nilai-mahasiswa";
    }

    @GetMapping("/mahasiswa/dashboard-penilaian")
    public String dashboardPenilaian(@RequestParam(required = false) String mk,
                                     @RequestParam(required = false) Integer idTugas,
                                     @RequestParam(required = false) Integer colorIndex,
                                     @AuthenticationPrincipal CustomUserDetails user,
                                     Model model) {
        
        logger.info("Accessing mahasiswa dashboard-penilaian: mk={}, idTugas={}, user={}", mk, idTugas, user.getIdUser());
        
        if (mk == null || mk.isEmpty()) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(mk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        List<MataKuliahMahasiswa> enrollments = mkmRepo.findByMataKuliah_KodeMKAndIsActive(mk, true);
        boolean isEnrolled = enrollments.stream()
            .anyMatch(e -> e.getUser().getIdUser().equals(user.getIdUser()));
        
        if (!isEnrolled) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
        
        List<TugasBesar> tugasList = tugasRepo.findByMataKuliah_KodeMKAndIsActive(mk, true);
        tugasList.sort(Comparator.comparing(TugasBesar::getJudulTugas));
        
        if (idTugas == null) {
            return "redirect:/mahasiswa/nilai?mk=" + mk + "&colorIndex=" + finalColorIndex;
        }
        
        model.addAttribute("user", user);
        model.addAttribute("kodeMk", mk);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("colorIndex", finalColorIndex);
        model.addAttribute("tugasList", tugasList);
        model.addAttribute("idTugas", idTugas);

        return "nilai/Mahasiswa/dashboard-nilai-mahasiswa";
    }

    @GetMapping("/mahasiswa/pemberian-nilai")
    public String pemberianNilai(@RequestParam(required = false) String mk,
                                 @RequestParam(required = false) Integer idTugas,
                                 @RequestParam(required = false) Integer colorIndex,
                                 @AuthenticationPrincipal CustomUserDetails user,
                                 Model model) {
        
        logger.info("Accessing mahasiswa pemberian-nilai: mk={}, idTugas={}, user={}", mk, idTugas, user.getIdUser());
        
        if (mk == null || mk.isEmpty() || idTugas == null || idTugas <= 0) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(mk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        List<MataKuliahMahasiswa> enrollments = mkmRepo.findByMataKuliah_KodeMKAndIsActive(mk, true);
        boolean isEnrolled = enrollments.stream()
            .anyMatch(e -> e.getUser().getIdUser().equals(user.getIdUser()));
        
        if (!isEnrolled) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
        if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(mk)) {
            return "redirect:/mahasiswa/nilai?mk=" + mk;
        }

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;

        model.addAttribute("user", user);
        model.addAttribute("kodeMk", mk);
        model.addAttribute("idTugas", idTugas);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("tugas", tugas);
        model.addAttribute("colorIndex", finalColorIndex);

        Nilai nilaiPribadi = nilaiRepository
            .findByUser_IdUserAndTugas_IdTugas(user.getIdUser(), idTugas)
            .orElse(null);

        if (nilaiPribadi != null) {

            Map<String, Object> nilaiPribadiData = new HashMap<>();
            nilaiPribadiData.put("nilaiAkhir", nilaiPribadi.getNilaiPribadi());
            
            List<Map<String, Object>> komponenList = new ArrayList<>();
            if (nilaiPribadi.getNilaiKomponenList() != null) {
                for (com.Tubeslayer.entity.NilaiKomponen nk : nilaiPribadi.getNilaiKomponenList()) {
                    Map<String, Object> komponenData = new HashMap<>();
                    komponenData.put("namaKomponen", nk.getKomponen().getNamaKomponen());
                    komponenData.put("bobot", nk.getKomponen().getBobot());
                    komponenData.put("nilai", nk.getNilaiKomponen());
                    komponenData.put("catatan", nk.getKomponen().getCatatan());
                    komponenList.add(komponenData);
                }
            }
            nilaiPribadiData.put("komponenList", komponenList);
            model.addAttribute("nilaiPribadi", nilaiPribadiData);
        }

        Integer idKelompok = kelompokJdbcService.getIdKelompok(idTugas, user.getIdUser());
        if (idKelompok != null && idKelompok > 0) {
            Kelompok kelompok = kelompokRepo.findById(idKelompok).orElse(null);
            if (kelompok != null) {
                model.addAttribute("namaKelompok", kelompok.getNamaKelompok());

                List<Nilai> nilaiKelompokList = nilaiRepository.findByTugasAndKelompok(idTugas, idKelompok);
                
                Nilai nilaiKelompokData = null;
                for (Nilai n : nilaiKelompokList) {
                    if (n.getNilaiKelompok() > 0) {
                        nilaiKelompokData = n;
                        break;
                    }
                }

                if (nilaiKelompokData != null) {
                    Map<String, Object> nilaiKelompokInfo = new HashMap<>();
                    nilaiKelompokInfo.put("nilaiAkhir", nilaiKelompokData.getNilaiKelompok());
                    
                    List<Map<String, Object>> komponenList = new ArrayList<>();
                    if (nilaiKelompokData.getNilaiKomponenList() != null) {
                        for (com.Tubeslayer.entity.NilaiKomponen nk : nilaiKelompokData.getNilaiKomponenList()) {
                            Map<String, Object> komponenData = new HashMap<>();
                            komponenData.put("namaKomponen", nk.getKomponen().getNamaKomponen());
                            komponenData.put("bobot", nk.getKomponen().getBobot());
                            komponenData.put("nilai", nk.getNilaiKomponen());
                            komponenData.put("catatan", nk.getKomponen().getCatatan());
                            komponenList.add(komponenData);
                        }
                    }
                    nilaiKelompokInfo.put("komponenList", komponenList);
                    model.addAttribute("nilaiKelompok", nilaiKelompokInfo);
                }
            }
        }

        return "nilai/Mahasiswa/pemberian-nilai-mahasiswa";
    }

    @GetMapping("/mahasiswa/lihat-nilai")
    public String lihatNilai(@RequestParam(required = false) String mk,
                            @RequestParam(required = false) Integer idTugas,
                            @RequestParam(required = false) Integer colorIndex,
                            @AuthenticationPrincipal CustomUserDetails user,
                            Model model) {
        
        logger.info("Accessing mahasiswa lihat-nilai: mk={}, idTugas={}, user={}", mk, idTugas, user.getIdUser());
        
        if (mk == null || mk.isEmpty() || idTugas == null || idTugas <= 0) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(mk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        List<MataKuliahMahasiswa> enrollments = mkmRepo.findByMataKuliah_KodeMKAndIsActive(mk, true);
        boolean isEnrolled = enrollments.stream()
            .anyMatch(e -> e.getUser().getIdUser().equals(user.getIdUser()));
        
        if (!isEnrolled) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
        if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(mk)) {
            return "redirect:/mahasiswa/nilai?mk=" + mk;
        }

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;

        model.addAttribute("user", user);
        model.addAttribute("kodeMk", mk);
        model.addAttribute("idTugas", idTugas);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("tugas", tugas);
        model.addAttribute("colorIndex", finalColorIndex);

        Nilai nilaiPribadi = nilaiRepository
            .findByUser_IdUserAndTugas_IdTugas(user.getIdUser(), idTugas)
            .orElse(null);

        if (nilaiPribadi != null) {

            Map<String, Object> nilaiPribadiData = new HashMap<>();
            nilaiPribadiData.put("nilaiAkhir", nilaiPribadi.getNilaiPribadi());
            
            List<Map<String, Object>> komponenList = new ArrayList<>();
            if (nilaiPribadi.getNilaiKomponenList() != null) {
                for (com.Tubeslayer.entity.NilaiKomponen nk : nilaiPribadi.getNilaiKomponenList()) {
                    Map<String, Object> komponenData = new HashMap<>();
                    komponenData.put("namaKomponen", nk.getKomponen().getNamaKomponen());
                    komponenData.put("bobot", nk.getKomponen().getBobot());
                    komponenData.put("nilai", nk.getNilaiKomponen());
                    komponenData.put("catatan", nk.getKomponen().getCatatan());
                    komponenList.add(komponenData);
                }
            }
            nilaiPribadiData.put("komponenList", komponenList);
            model.addAttribute("nilaiPribadi", nilaiPribadiData);
        }

        Integer idKelompok = kelompokJdbcService.getIdKelompok(idTugas, user.getIdUser());
        if (idKelompok != null && idKelompok > 0) {
            Kelompok kelompok = kelompokRepo.findById(idKelompok).orElse(null);
            if (kelompok != null) {
                model.addAttribute("namaKelompok", kelompok.getNamaKelompok());

                List<Nilai> nilaiKelompokList = nilaiRepository.findByTugasAndKelompok(idTugas, idKelompok);
                
                Nilai nilaiKelompokData = null;
                for (Nilai n : nilaiKelompokList) {
                    if (n.getNilaiKelompok() > 0) {
                        nilaiKelompokData = n;
                        break;
                    }
                }

                if (nilaiKelompokData != null) {
                    Map<String, Object> nilaiKelompokInfo = new HashMap<>();
                    nilaiKelompokInfo.put("nilaiAkhir", nilaiKelompokData.getNilaiKelompok());
                    
                    List<Map<String, Object>> komponenList = new ArrayList<>();
                    if (nilaiKelompokData.getNilaiKomponenList() != null) {
                        for (com.Tubeslayer.entity.NilaiKomponen nk : nilaiKelompokData.getNilaiKomponenList()) {
                            Map<String, Object> komponenData = new HashMap<>();
                            komponenData.put("namaKomponen", nk.getKomponen().getNamaKomponen());
                            komponenData.put("bobot", nk.getKomponen().getBobot());
                            komponenData.put("nilai", nk.getNilaiKomponen());
                            komponenData.put("catatan", nk.getKomponen().getCatatan());
                            komponenList.add(komponenData);
                        }
                    }
                    nilaiKelompokInfo.put("komponenList", komponenList);
                    model.addAttribute("nilaiKelompok", nilaiKelompokInfo);
                }
            }
        }

        return "nilai/Mahasiswa/lihat-nilai-mahasiswa";
    }

    @GetMapping("/mahasiswa/rubrik-penilaian")
    public String mahasiswaRubrikPenilaian(
            @RequestParam(required = false) String mk,
            @RequestParam(required = false) Integer idTugas,
            @RequestParam(required = false) Integer colorIndex,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        
        if (mk == null || mk.isEmpty() || idTugas == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(mk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        List<MataKuliahMahasiswa> enrollments = mkmRepo.findByMataKuliah_KodeMKAndIsActive(mk, true);
        boolean isEnrolled = enrollments.stream()
            .anyMatch(e -> e.getUser().getIdUser().equals(user.getIdUser()));
        
        if (!isEnrolled) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
        if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(mk)) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        List<Map<String, Object>> rubrikItems = new ArrayList<>();
        int totalBobot = 0;
        boolean hasRubrik = false;
        
        if (tugas.getRubrik() != null) {
            hasRubrik = true;
            RubrikNilai rubrik = tugas.getRubrik();
            
            List<KomponenNilai> komponenList = komponenNilaiRepo.findByRubrik_IdRubrik(rubrik.getIdRubrik());
            
            if (komponenList != null && !komponenList.isEmpty()) {
                for (KomponenNilai komponen : komponenList) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("namaKomponen", komponen.getNamaKomponen());
                    item.put("bobot", komponen.getBobot());
                    item.put("catatan", komponen.getCatatan() != null ? komponen.getCatatan() : "");
                    rubrikItems.add(item);
                    totalBobot += komponen.getBobot();
                }
            }
        }

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
        
        model.addAttribute("user", user);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("tugas", tugas);
        model.addAttribute("kodeMk", mk);
        model.addAttribute("idTugas", idTugas);
        model.addAttribute("colorIndex", finalColorIndex);
        model.addAttribute("rubrikItems", rubrikItems);
        model.addAttribute("totalBobot", totalBobot);
        model.addAttribute("hasRubrik", hasRubrik);

        return "nilai/Mahasiswa/rubrik-penilaian-mahasiswa";
    }

    @GetMapping("/mahasiswa/jadwal-penilaian")
    public String mahasiswaJadwalPenilaian(
            @RequestParam(required = false) String mk,
            @RequestParam(required = false) Integer idTugas,
            @RequestParam(required = false) Integer colorIndex,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        
        if (mk == null || mk.isEmpty() || idTugas == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        MataKuliah mataKuliah = mataKuliahRepo.findById(mk).orElse(null);
        if (mataKuliah == null) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        List<MataKuliahMahasiswa> enrollments = mkmRepo.findByMataKuliah_KodeMKAndIsActive(mk, true);
        boolean isEnrolled = enrollments.stream()
            .anyMatch(e -> e.getUser().getIdUser().equals(user.getIdUser()));
        
        if (!isEnrolled) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
        if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(mk)) {
            return "redirect:/mahasiswa/mata-kuliah";
        }

        int finalColorIndex = (colorIndex != null && colorIndex >= 0) ? colorIndex : 0;
        
        model.addAttribute("user", user);
        model.addAttribute("mataKuliah", mataKuliah);
        model.addAttribute("tugas", tugas);
        model.addAttribute("kodeMk", mk);
        model.addAttribute("idTugas", idTugas);
        model.addAttribute("colorIndex", finalColorIndex);

        return "nilai/Mahasiswa/jadwal-penilaian-mahasiswa";
    }

    @GetMapping("/mahasiswa/jadwal-penilaian/get")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getJadwalPenilaianMahasiswa(
            @RequestParam(required = false) String mk,
            @RequestParam(required = false) Integer idTugas,
            @AuthenticationPrincipal CustomUserDetails user) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (mk == null || mk.isEmpty() || idTugas == null) {
                response.put("success", false);
                response.put("message", "Parameter tidak lengkap");
                return ResponseEntity.ok(response);
            }

            List<MataKuliahMahasiswa> enrollments = mkmRepo.findByMataKuliah_KodeMKAndIsActive(mk, true);
            boolean isEnrolled = enrollments.stream()
                .anyMatch(e -> e.getUser().getIdUser().equals(user.getIdUser()));
            
            if (!isEnrolled) {
                response.put("success", false);
                response.put("message", "Anda tidak terdaftar di mata kuliah ini");
                return ResponseEntity.ok(response);
            }

            TugasBesar tugas = tugasRepo.findById(idTugas).orElse(null);
            if (tugas == null || !tugas.getMataKuliah().getKodeMK().equals(mk) || !tugas.isActive()) {
                response.put("success", false);
                response.put("message", "Tugas tidak ditemukan");
                return ResponseEntity.ok(response);
            }

            RubrikNilai rubrik = tugas.getRubrik();
            List<Map<String, Object>> jadwalList = new ArrayList<>();

            if (rubrik != null && rubrik.getKomponenList() != null) {
                for (KomponenNilai komponen : rubrik.getKomponenList()) {
                    Map<String, Object> jadwal = new HashMap<>();
                    jadwal.put("idTugas", komponen.getIdKomponen());
                    jadwal.put("judulTugas", komponen.getNamaKomponen());
                    jadwal.put("deadline", tugas.getDeadline().toString());
                    jadwalList.add(jadwal);
                }
            }

            response.put("success", true);
            response.put("jadwalList", jadwalList);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting jadwal penilaian mahasiswa: ", e);
            response.put("success", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}