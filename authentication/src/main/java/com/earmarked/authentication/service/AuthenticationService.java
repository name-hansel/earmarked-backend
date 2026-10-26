package com.earmarked.authentication.service;

import com.earmarked.authentication.dto.UserDTO;
import com.earmarked.authentication.dto.UserDeletedEvent;
import com.earmarked.authentication.dto.delete_account.DeleteAccountRequest;
import com.earmarked.authentication.dto.login.LoginRequest;
import com.earmarked.authentication.dto.result.TokenPair;
import com.earmarked.authentication.exception.InvalidRefreshTokenException;
import com.earmarked.authentication.service.email.EmailService;
import com.earmarked.authentication.service.jwt.JwtService;
import com.earmarked.authentication.service.jwt.RefreshTokenService;
import com.earmarked.authentication.service.token.AccountActivationTokenService;
import com.earmarked.authentication.service.token.PasswordResetTokenService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final AccountActivationTokenService accountActivationTokenService;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;

    public void register(String email, String password, String username) {
        String trimmedEmail = email.trim();
        String trimmedUsername = username.trim();
        UserDTO user = userService.createNewUser(trimmedEmail, trimmedUsername, password);

        sendAccountActivationEmail(user);
    }

    @Transactional
    public void activateAccount(String token) {
        UserDTO user = userService.activateAccount(token);
        emailService.sendAccountActivatedEmail(user.email());
    }

    public TokenPair login(LoginRequest loginRequest) {
        String email = loginRequest.email().trim();

        UUID userId = userService.authenticateUser(email, loginRequest.password()).userId();
        UUID refreshTokenUuid = refreshTokenService.createRefreshToken(userId);

        return new TokenPair(jwtService.generateAccessToken(userId),
                jwtService.generateRefreshToken(userId, refreshTokenUuid));
    }

    public UserDTO getCurrentUser(UUID userId) {
        return userService.getUser(userId);
    }

    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        UUID refreshTokenId;
        try {
            refreshTokenId = jwtService.extractTokenId(refreshToken);
        } catch (RuntimeException e) {
            // invalid or expired token: nothing to revoke, the client is logged out either way
            return;
        }

        refreshTokenService.revoke(refreshTokenId);
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

    @Transactional
    public void deleteAccount(DeleteAccountRequest deleteAccountRequest, UUID userId) {
        userService.deleteUser(userId, deleteAccountRequest.password());
        refreshTokenService.deleteAllForUser(userId);
        eventPublisher.publishEvent(new UserDeletedEvent(userId));
    }

    public void resendActivationEmail(String email) {
        String trimmedEmail = email.trim();
        UserDTO user = userService.findVerifiedUserByEmail(trimmedEmail);

        if (user != null) {
            sendAccountActivationEmail(user);
        }
    }

    @Transactional
    public void changePassword(UUID userId, String newPassword, String oldPassword, String refreshToken) {
        if (refreshToken == null) {
            throw new InvalidRefreshTokenException();
        }

        userService.changePassword(userId, newPassword, oldPassword);

        UUID refreshTokenId = jwtService.extractTokenId(refreshToken);
        refreshTokenService.deleteAllExcept(userId, refreshTokenId);
    }

    public void forgotPassword(String email) {
        String trimmedEmail = email.trim();
        UserDTO user = userService.initiateForgotPassword(trimmedEmail);

        if (user != null) {
            String passwordResetToken = passwordResetTokenService.generatePasswordResetToken(user.userId());
            emailService.sendForgotPasswordEmail(user.email(), passwordResetToken);
        }
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        UserDTO user = userService.resetPassword(token, newPassword);
        refreshTokenService.deleteAllForUser(user.userId());
        emailService.sendResetPasswordEmail(user.email());
    }

    private void sendAccountActivationEmail(UserDTO user) {
        String accountActivationToken = accountActivationTokenService.generateAccountActivationToken(user.userId());
        emailService.sendAccountActivationEmail(user.email(), accountActivationToken);
    }
}
