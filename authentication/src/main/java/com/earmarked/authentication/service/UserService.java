package com.earmarked.authentication.service;

import com.earmarked.authentication.dto.UserDTO;
import com.earmarked.authentication.entity.UserEntity;
import com.earmarked.authentication.exception.*;
import com.earmarked.authentication.repository.UserRepository;
import com.earmarked.authentication.service.token.AccountActivationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountActivationTokenService accountActivationTokenService;

    private final PasswordEncoder passwordEncoder;

    public UserDTO createNewUser(String email, String username, String password) {
        checkExistingUserForRegistration(email, username);

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));

        return transform(userRepository.save(user));
    }

    public UserDTO activateAccount(String token) {
        Optional<UUID> userUuid = accountActivationTokenService.consumeToken(token);

        if (userUuid.isEmpty()) {
            throw new InvalidAccountActivationTokenException();
        }

        UserEntity user = userRepository.findById(userUuid.get()).orElseThrow(UserNotFoundException::new);
        user.setVerified(true);

        return transform(user);
    }

    public UserDTO authenticateUser(String email, String password) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.filter(UserEntity::isVerified).isEmpty()) {
            throw new InvalidLoginException();
        }

        UserEntity user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidLoginException();
        }

        return transform(user);
    }

    public UserDTO getUser(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        
        return transform(user);
    }

    public void deleteUser(UUID userId, String password) {
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IncorrectPasswordException();
        }

        userRepository.deleteById(user.getId());
    }

    public UserDTO findVerifiedUserByEmail(String email) {
        Optional<UserEntity> unverifiedUserOptional = userRepository.findByEmailAndVerified(email, false);

        return unverifiedUserOptional.map(this::transform).orElse(null);
    }

    public void changePassword(UUID userId, String newPassword, String oldPassword) {
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IncorrectPasswordException();
        }

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new PasswordSameAsCurrentException();
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
    }

    public UserDTO initiateForgotPassword(String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmailAndVerified(email, true);

        return userOptional.map(this::transform).orElse(null);
    }

    private void checkExistingUserForRegistration(String email, String username) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException();
        }
    }

    private UserDTO transform(UserEntity user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}
