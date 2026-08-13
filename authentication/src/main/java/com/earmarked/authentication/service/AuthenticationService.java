package com.earmarked.authentication.service;

import com.earmarked.authentication.dto.RegisterRequest;
import com.earmarked.authentication.entity.UserEntity;
import com.earmarked.authentication.exception.DuplicateEmailException;
import com.earmarked.authentication.exception.DuplicateUsernameException;
import com.earmarked.authentication.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterRequest registerRequest) {
        String email = registerRequest.email().trim();

        if (userRepository.existsByEmail(email)) {
            UserEntity existingUser = userRepository.findByEmail(email);

            if (!existingUser.isVerified()) {
                // TODO: Send verification
                throw new DuplicateEmailException(true);
            }

            throw new DuplicateEmailException(false);
        }

        String username = registerRequest.username().trim();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException();
        }

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(registerRequest.password()));

        // TODO: Send email

        userRepository.save(user);
    }
}
