package com.partnerhub.config;

import com.partnerhub.domain.User;
import com.partnerhub.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeedConfig {

    private static final Logger log = LoggerFactory.getLogger(AdminSeedConfig.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner seedAdmin(UserRepository userRepository) {
        return args -> {
            String adminEmail = "admin@admin.com";
            String adminPassword = "admin"; // TODO: env
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setName("Admin");
                admin.setEnabled(true);
                // admin.setRole("ROLE_ADMIN"); // TODO: Implement
                userRepository.save(admin);
            }
        };
    }
}
