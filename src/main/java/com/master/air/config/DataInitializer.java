package com.master.air.config;

import com.master.air.model.Category;
import com.master.air.model.Role;
import com.master.air.model.User;
import com.master.air.repository.CategoryRepository;
import com.master.air.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final CategoryRepository categoryRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CategoryRepository cr, UserRepository ur, PasswordEncoder pe) {
        this.categoryRepo = cr; this.userRepo = ur; this.passwordEncoder = pe;
    }

    @Override
    public void run(String... args) {
        if (categoryRepo.count() == 0) {
            for (String l : new String[]{"Immobilier","Emploi","Vehicules","Electronique","Services","Divers"})
                categoryRepo.save(new Category(l));
            log.info("Categories par defaut creees");
        }
        if (!userRepo.existsByUsername("admin")) {
            userRepo.save(new User("admin", "admin@master.fr", passwordEncoder.encode("admin"), Role.ROLE_ADMIN));
            log.info("Admin cree");
        }
        if (!userRepo.existsByUsername("user")) {
            userRepo.save(new User("user", "user@master.fr", passwordEncoder.encode("user"), Role.ROLE_USER));
            log.info("User cree");
        }
    }
}
