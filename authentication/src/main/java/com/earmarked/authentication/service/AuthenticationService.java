package com.earmarked.authentication.service;

import com.earmarked.authentication.dto.delete_account.DeleteAccountRequest;
import com.earmarked.authentication.dto.login.LoginRequest;
import com.earmarked.authentication.dto.me.MeResponse;
import com.earmarked.authentication.dto.register.RegisterRequest;
import com.earmarked.authentication.dto.register.RegisterResponse;
import com.earmarked.authentication.dto.resend_activation.ResendActivationResponse;
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

    public RegisterResponse register(RegisterRequest registerRequest) {
        String email = registerRequest.email().trim();
        String username = registerRequest.username().trim();
        userService.createNewUser(email, username, registerRequest.password());
        return new RegisterResponse("Account created successfully. Please check your inbox for verification.");
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

    public ResendActivationResponse resendActivationEmail(String email) {
        userService.resendActivationEmail(email);
        return new ResendActivationResponse(
                "If an account with this email address exists, an activation link has been sent.");
    }
}
