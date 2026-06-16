package com.jubilee.workit.config;

import com.jubilee.workit.entity.User;
import com.jubilee.workit.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultUserInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${workit.default-user.email:}")
    private String defaultEmail;

    @Value("${workit.default-user.password:}")
    private String defaultPassword;

    @Value("${workit.default-user.role:}")
    private String defaultRole;

    public DefaultUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (defaultEmail == null || defaultEmail.isBlank()
                || defaultPassword == null || defaultPassword.isBlank()) {
            log.info("Default user creation skipped. Set WORKIT_DEFAULT_USER_EMAIL and WORKIT_DEFAULT_USER_PASSWORD to enable it.");
            return;
        }

        User user = userRepository.findByEmail(defaultEmail).orElseGet(User::new);
        user.setEmail(defaultEmail);
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setLoginType("EMAIL");
        if (defaultRole != null && !defaultRole.isBlank()) {
            user.setRole(normalizeRole(defaultRole));
        }

        userRepository.save(user);
        log.info("Default user is ready: {}", defaultEmail);
    }

    private String normalizeRole(String role) {
        if ("JOBSEEKER".equalsIgnoreCase(role)) {
            return "JOBSEEKER";
        }
        return "EMPLOYER";
    }
}
