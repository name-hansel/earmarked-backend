package com.earmarked.authentication.service;

import com.earmarked.authentication.dto.delete_account.DeleteAccountRequest;
import com.earmarked.authentication.dto.login.LoginRequest;
import com.earmarked.authentication.dto.me.MeResponse;
import com.earmarked.authentication.dto.result.TokenPair;
import com.earmarked.authentication.exception.InvalidRefreshTokenException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public void register(String email, String password, String username) {
        String trimmedEmail = email.trim();
        String trimmedUsername = username.trim();
        userService.createNewUser(trimmedEmail, trimmedUsername, password);
    }

    @Transactional
    public void activateAccount(String token) {
        userService.activateAccount(token);
    }

    public TokenPair login(LoginRequest loginRequest) {
        String email = loginRequest.email().trim();

        UUID userId = userService.authenticateUser(email, loginRequest.password());
        UUID refreshTokenUuid = refreshTokenService.createRefreshToken(userId);

        return new TokenPair(jwtService.generateAccessToken(userId),
                jwtService.generateRefreshToken(userId, refreshTokenUuid));
    }

    public MeResponse getCurrentUser(UUID userId) {
        return userService.getUserData(userId);
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

    @Transactional
    public void deleteAccount(DeleteAccountRequest deleteAccountRequest, UUID userId) {
        userService.deleteUser(userId, deleteAccountRequest.password());
        refreshTokenService.deleteAllForUser(userId);
        // TODO - delete other related entities
    }

    public void resendActivationEmail(String email) {
        userService.resendActivationEmail(email);
    }

    public void changePassword(String newPassword, String oldPassword) {
    }
}
