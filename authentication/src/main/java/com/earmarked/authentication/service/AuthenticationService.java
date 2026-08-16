package com.earmarked.authentication.service;

import com.earmarked.authentication.dto.register.RegisterRequest;
import com.earmarked.authentication.entity.UserEntity;
import com.earmarked.authentication.exception.DuplicateEmailException;
import com.earmarked.authentication.exception.DuplicateUsernameException;
import com.earmarked.authentication.exception.InvalidAccountActivationTokenException;
import com.earmarked.authentication.exception.UserNotFoundException;
import com.earmarked.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    public void activateAccount(String token) {
        Optional<UUID> userUuid = accountActivationTokenService.validateToken(token);

        if (userUuid.isEmpty()) {
            throw new InvalidAccountActivationTokenException();
        }

        UserEntity user = userRepository.findById(userUuid.get()).orElseThrow(UserNotFoundException::new);
        user.setVerified(true);

        // TODO: Send email
    }
}
