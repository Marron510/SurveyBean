package com.example.SurveyBean.config;

import com.example.SurveyBean.domain.User;
import com.example.SurveyBean.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("testuser").isEmpty()) {
            User testUser = User.builder()
                    .username("testuser")
                    .email("test@example.com")
                    .password("password") // 실제 앱에서는 인코딩되어야 함.
                    .roles("ROLE_ADMIN")
                    .build();
            userRepository.save(testUser);
        }
    }
}
