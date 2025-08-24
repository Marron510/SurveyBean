package com.example.SurveyBean;

import com.example.SurveyBean.domain.User;
import com.example.SurveyBean.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Use @Lazy on PasswordEncoder to break circular dependency with SecurityConfig
    public DataLoader(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if a default user already exists
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("password"))
                    .roles("ROLE_USER") // Add this line
                    .build();
            userRepository.save(user);
            System.out.println("Default user created with ROLE_USER!");
        }
    }
}
