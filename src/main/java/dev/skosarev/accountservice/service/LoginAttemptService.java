package dev.skosarev.accountservice.service;

import dev.skosarev.accountservice.model.User;
import dev.skosarev.accountservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginAttemptService {
    public static final int MAX_FAILED_ATTEMPTS = 4;
    private final UserRepository userRepository;

    @Autowired
    public LoginAttemptService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void loginSuccess(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseGet(User::new);
        user.setFailedAttempt(0);
        userRepository.save(user);
    }

    public void loginFailure(String email, String uri) {
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);
        if (userOptional.isEmpty()) {
            return;
        }
        User user = userOptional.get();
        if (user.containsRole("ROLE_ADMINISTRATOR")) {
            return;
        }

        user.setFailedAttempt(user.getFailedAttempt() + 1);

        if (user.getFailedAttempt() > MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
        }
        userRepository.save(user);
    }
}
