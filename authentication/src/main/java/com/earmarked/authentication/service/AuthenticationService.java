package com.earmarked.authentication.service;

import com.earmarked.authentication.dto.login.LoginRequest;
import com.earmarked.authentication.dto.me.MeResponse;
import com.earmarked.authentication.dto.register.RegisterRequest;
import com.earmarked.authentication.dto.result.TokenPair;
import com.earmarked.authentication.entity.UserEntity;
import com.earmarked.authentication.exception.*;
import com.earmarked.authentication.repository.UserRepository;
import com.earmarked.authentication.service.email.EmailService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AccountActivationTokenService accountActivationTokenService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest registerRequest) {
        String email = registerRequest.email().trim();

        Optional<UserEntity> existingUserOptional = userRepository.findByEmail(email);
        if (existingUserOptional.isPresent()) {
            UserEntity existingUser = existingUserOptional.get();

            if (!existingUser.isVerified()) {
                // Resend activation link
                emailService.sendAccountActivationEmail(email,
                        accountActivationTokenService.generateAccountActivationToken(existingUser.getId()));
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

        emailService.sendAccountActivationEmail(email,
                accountActivationTokenService.generateAccountActivationToken(savedUser.getId()));
    }

    @Transactional
    public void activateAccount(String token) {
        Optional<UUID> userUuid = accountActivationTokenService.validateToken(token);

        if (userUuid.isEmpty()) {
            throw new InvalidAccountActivationTokenException();
        }

        UserEntity user = userRepository.findById(userUuid.get()).orElseThrow(UserNotFoundException::new);
        user.setVerified(true);
        emailService.sendAccountActivatedEmail(user.getEmail());
    }

    public TokenPair login(LoginRequest loginRequest) {
        String email = loginRequest.email().trim();
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.filter(UserEntity::isVerified).isEmpty()) {
            throw new InvalidLoginException();
        }

        UserEntity user = userOptional.get();
        if (!passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())) {
            throw new InvalidLoginException();
        }

        UUID refreshTokenUuid = refreshTokenService.createRefreshToken(user.getId());

        return new TokenPair(jwtService.generateAccessToken(user.getId()),
                jwtService.generateRefreshToken(user.getId(), refreshTokenUuid));
    }

    public MeResponse getCurrentUser(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return new MeResponse(user.getId(), user.getUsername());
    }

    public void logout(String refreshToken) {
        UUID refreshTokenUuid = jwtService.extractTokenId(refreshToken);
        refreshTokenService.revoke(refreshTokenUuid);
    }

    public TokenPair refresh(String oldRefreshToken) {
        if (oldRefreshToken == null) {
            throw new InvalidRefreshTokenException();
        }

        try {
            UUID oldRefreshTokenUuid = jwtService.extractTokenId(oldRefreshToken);
            UUID userId = refreshTokenService.findValidTokenUserId(oldRefreshTokenUuid);
            UUID newRefreshTokenUuid = refreshTokenService.rotate(oldRefreshTokenUuid);

            return new TokenPair(jwtService.generateAccessToken(userId),
                    jwtService.generateRefreshToken(userId, newRefreshTokenUuid));
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidRefreshTokenException();
        }
    }
}
