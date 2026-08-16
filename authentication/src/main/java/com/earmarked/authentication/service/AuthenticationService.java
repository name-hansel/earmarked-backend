package com.earmarked.authentication.service;

import com.earmarked.authentication.dto.register.RegisterRequest;
import com.earmarked.authentication.entity.UserEntity;
import com.earmarked.authentication.exception.DuplicateEmailException;
import com.earmarked.authentication.exception.DuplicateUsernameException;
import com.earmarked.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AccountActivationTokenService accountActivationTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        UserEntity savedUser = userRepository.save(user);

        String token = accountActivationTokenService.generateAccountActivationToken(savedUser.getId());
        // TODO: Send email
    }
}
