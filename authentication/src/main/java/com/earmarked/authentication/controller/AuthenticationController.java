package com.earmarked.authentication.controller;

import com.earmarked.authentication.config.ApiPaths;
import com.earmarked.authentication.config.jwt.JwtConfiguration;
import com.earmarked.authentication.dto.UserDTO;
import com.earmarked.authentication.dto.activate_account.ActivateAccountRequest;
import com.earmarked.authentication.dto.change_password.ChangePasswordRequest;
import com.earmarked.authentication.dto.change_password.ChangePasswordResponse;
import com.earmarked.authentication.dto.delete_account.DeleteAccountRequest;
import com.earmarked.authentication.dto.forgot_password.ForgotPasswordRequest;
import com.earmarked.authentication.dto.forgot_password.ForgotPasswordResponse;
import com.earmarked.authentication.dto.login.LoginRequest;
import com.earmarked.authentication.dto.me.MeResponse;
import com.earmarked.authentication.dto.register.RegisterRequest;
import com.earmarked.authentication.dto.register.RegisterResponse;
import com.earmarked.authentication.dto.resend_activation.ResendActivationRequest;
import com.earmarked.authentication.dto.resend_activation.ResendActivationResponse;
import com.earmarked.authentication.dto.reset_password.ResetPasswordRequest;
import com.earmarked.authentication.dto.reset_password.ResetPasswordResponse;
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
        authenticationService.register(registerRequest.email(), registerRequest.password(), registerRequest.username());

        return ResponseEntity.ok(
                new RegisterResponse("Account created successfully. Please check your inbox for verification."));
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
        UserDTO currentUser = authenticationService.getCurrentUser(userId);

        return ResponseEntity.ok(new MeResponse(currentUser.userId(), currentUser.username()));
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
        authenticationService.resendActivationEmail(resendActivationRequest.email());
        
        return ResponseEntity.ok(new ResendActivationResponse(
                "If an account with this email address exists, an activation link has been sent."));
    }

    @PatchMapping(ApiPaths.CHANGE_PASSWORD)
    public ResponseEntity<ChangePasswordResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, @AuthenticationPrincipal UUID userId, @CookieValue(name = JwtConfiguration.REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        authenticationService.changePassword(userId, changePasswordRequest.newPassword(),
                changePasswordRequest.oldPassword(), refreshToken);

        return ResponseEntity.ok(new ChangePasswordResponse("Password changed successfully."));
    }

    @PostMapping(ApiPaths.FORGOT_PASSWORD)
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.forgotPassword(forgotPasswordRequest.email());

        return ResponseEntity.accepted().body(new ForgotPasswordResponse(
                "If an account with this email address exists, a password reset link has been sent."));
    }

    @PostMapping(ApiPaths.RESET_PASSWORD)
    public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authenticationService.resetPassword(resetPasswordRequest.token(), resetPasswordRequest.newPassword());

        return ResponseEntity.ok(new ResetPasswordResponse("Password reset successfully."));
    }
}
