package com.earmarked.authentication.service;

import com.earmarked.authentication.dto.me.MeResponse;
import com.earmarked.authentication.entity.UserEntity;
import com.earmarked.authentication.exception.*;
import com.earmarked.authentication.repository.UserRepository;
import com.earmarked.authentication.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AccountActivationTokenService accountActivationTokenService;

    private final PasswordEncoder passwordEncoder;

    public void createNewUser(String email, String username, String password) {
        checkExistingUserForRegistration(email, username);

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));

        UserEntity savedUser = userRepository.save(user);
        sendAccountActivationEmail(savedUser);
    }

    public void activateAccount(String token) {
        Optional<UUID> userUuid = accountActivationTokenService.validateToken(token);

        if (userUuid.isEmpty()) {
            throw new InvalidAccountActivationTokenException();
        }

        UserEntity user = userRepository.findById(userUuid.get()).orElseThrow(UserNotFoundException::new);
        user.setVerified(true);
        emailService.sendAccountActivatedEmail(user.getEmail());
    }

    public UUID authenticateUser(String email, String password) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.filter(UserEntity::isVerified).isEmpty()) {
            throw new InvalidLoginException();
        }

        UserEntity user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidLoginException();
        }

        return user.getId();
    }

    public MeResponse getUserData(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return new MeResponse(user.getId(), user.getUsername());
    }

    public void deleteUser(UUID userId, String password) {
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IncorrectPasswordException();
        }

        userRepository.deleteById(user.getId());
    }

    public void resendActivationEmail(String email) {
        Optional<UserEntity> unverifiedUserOptional = userRepository.findByEmailAndVerifiedFalse(email);

        if (unverifiedUserOptional.isEmpty()) {
            return;
        }

        sendAccountActivationEmail(unverifiedUserOptional.get());
    }

    private void checkExistingUserForRegistration(String email, String username) {
        Optional<UserEntity> existingUserOptional = userRepository.findByEmail(email);
        if (existingUserOptional.isPresent()) {
            UserEntity existingUser = existingUserOptional.get();

            if (!existingUser.isVerified()) {
                // Resend activation link
                sendAccountActivationEmail(existingUser);
                throw new DuplicateEmailException(true);
            }

            throw new DuplicateEmailException(false);
        }

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException();
        }
    }

    private void sendAccountActivationEmail(UserEntity user) {
        String accountActivationToken = accountActivationTokenService.generateAccountActivationToken(user.getId());
        emailService.sendAccountActivationEmail(user.getEmail(), accountActivationToken);
    }
}
