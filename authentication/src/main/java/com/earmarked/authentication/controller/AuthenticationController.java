package com.earmarked.authentication.controller;

import com.earmarked.authentication.config.ApiPaths;
import com.earmarked.authentication.config.jwt.JwtConfiguration;
import com.earmarked.authentication.dto.activate_account.ActivateAccountRequest;
import com.earmarked.authentication.dto.delete_account.DeleteAccountRequest;
import com.earmarked.authentication.dto.login.LoginRequest;
import com.earmarked.authentication.dto.me.MeResponse;
import com.earmarked.authentication.dto.register.RegisterRequest;
import com.earmarked.authentication.dto.register.RegisterResponse;
import com.earmarked.authentication.dto.resend_activation.ResendActivationRequest;
import com.earmarked.authentication.dto.resend_activation.ResendActivationResponse;
import com.earmarked.authentication.dto.result.TokenPair;
import com.earmarked.authentication.service.AuthenticationService;
import com.earmarked.authentication.service.JwtCookieService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping(path = ApiPaths.AUTH)
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtCookieService jwtCookieService;

    @PostMapping(ApiPaths.REGISTER)
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping(ApiPaths.ACTIVATE_ACCOUNT)
    public ResponseEntity<Void> activateAccount(@RequestBody @Valid ActivateAccountRequest activateAccountRequest) {
        authenticationService.activateAccount(activateAccountRequest.token());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(ApiPaths.LOGIN)
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        TokenPair tokenPair = authenticationService.login(loginRequest);
        jwtCookieService.addAccessTokenCookie(tokenPair.accessToken(), httpServletResponse);
        jwtCookieService.addRefreshTokenCookie(tokenPair.refreshToken(), httpServletResponse);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiPaths.ME)
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(authenticationService.getCurrentUser(userId));
    }

    @PostMapping(ApiPaths.LOGOUT)
    public ResponseEntity<Void> logout(@CookieValue(name = JwtConfiguration.REFRESH_TOKEN_COOKIE, required = false) String refreshToken, HttpServletResponse httpServletResponse) {
        authenticationService.logout(refreshToken);
        jwtCookieService.clearCookies(httpServletResponse);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(ApiPaths.REFRESH)
    public ResponseEntity<Void> refresh(@CookieValue(name = JwtConfiguration.REFRESH_TOKEN_COOKIE, required = false) String refreshToken, HttpServletResponse httpServletResponse) {
        TokenPair tokenPair = authenticationService.refresh(refreshToken);
        jwtCookieService.addAccessTokenCookie(tokenPair.accessToken(), httpServletResponse);
        jwtCookieService.addRefreshTokenCookie(tokenPair.refreshToken(), httpServletResponse);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(ApiPaths.DELETE_ACCOUNT)
    public ResponseEntity<Void> deleteAccount(@Valid @RequestBody DeleteAccountRequest deleteAccountRequest, @AuthenticationPrincipal UUID userId, HttpServletResponse httpServletResponse) {
        authenticationService.deleteAccount(deleteAccountRequest, userId);
        jwtCookieService.clearCookies(httpServletResponse);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(ApiPaths.RESEND_ACTIVATION)
    public ResponseEntity<ResendActivationResponse> resendActivation(@Valid @RequestBody ResendActivationRequest resendActivationRequest) {
        return ResponseEntity.ok(authenticationService.resendActivationEmail(resendActivationRequest.email()));
    }
}
