package com.example.openTable.util;

import com.example.openTable.Enum.Role;
import com.example.openTable.model.User;
import com.example.openTable.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//Creates admin account
@Component
public class AdminUserInitializer {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Bean
    public CommandLineRunner initAdminUser(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
                User admin = new User();
                admin.setName("Admin");
                admin.setSurname("User");
                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
