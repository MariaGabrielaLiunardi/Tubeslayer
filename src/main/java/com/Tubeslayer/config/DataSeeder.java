    package com.Tubeslayer.config;

    import com.Tubeslayer.entity.User;
    import com.Tubeslayer.repository.UserRepository;
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.crypto.password.PasswordEncoder;

    @Configuration
    public class DataSeeder {

        @Bean
        CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
            return args -> {
                // ADMIN
                if (userRepository.findByEmail("admin@unpar.ac.id").isEmpty()) {
                    User admin = new User();
                    admin.setIdUser("20230101");
                    admin.setEmail("admin@unpar.ac.id");
                    admin.setPassword(passwordEncoder.encode("admin123")); // login pakai "admin123"
                    admin.setNama("Administrator");
                    admin.setRole("Admin");
                    admin.setActive(true);
                    userRepository.save(admin);
                    System.out.println("User ADMIN berhasil ditambahkan!");
                }

                // DOSEN
                if (userRepository.findByEmail("clarajoycelene@unpar.ac.id").isEmpty()) {
                    User dosen = new User();
                    dosen.setIdUser("20220304");
                    dosen.setEmail("clarajoycelene@unpar.ac.id");
                    dosen.setPassword(passwordEncoder.encode("dosen123")); // login pakai "dosen123"
                    dosen.setNama("Clara Joycelyne Siauttara");
                    dosen.setRole("Dosen");
                    dosen.setActive(true);
                    userRepository.save(dosen);
                    System.out.println("User DOSEN berhasil ditambahkan!");
                }

                // MAHASISWA
                if (userRepository.findByEmail("keishaneira@student.unpar.ac.id").isEmpty()) {
                    User mahasiswa = new User();
                    mahasiswa.setIdUser("6182301048");
                    mahasiswa.setEmail("keishaneira@student.unpar.ac.id");
                    mahasiswa.setPassword(passwordEncoder.encode("mhs123")); // login pakai "mhs123"
                    mahasiswa.setNama("Keisha Neira Joycelyn");
                    mahasiswa.setRole("Mahasiswa");
                    mahasiswa.setActive(true);
                    userRepository.save(mahasiswa);
                    System.out.println("User MAHASISWA berhasil ditambahkan!");
                }
            };
        }
    }
