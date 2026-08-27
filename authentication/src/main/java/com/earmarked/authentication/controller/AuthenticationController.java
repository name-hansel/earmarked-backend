package com.earmarked.authentication.controller;

import com.earmarked.authentication.config.ApiPaths;
import com.earmarked.authentication.dto.activate_account.ActivateAccountRequest;
import com.earmarked.authentication.dto.login.LoginRequest;
import com.earmarked.authentication.dto.register.RegisterRequest;
import com.earmarked.authentication.dto.register.RegisterResponse;
import com.earmarked.authentication.dto.result.AuthenticationResult;
import com.earmarked.authentication.service.AuthenticationService;
import com.earmarked.authentication.service.JwtCookieService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(path = ApiPaths.AUTH)
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtCookieService jwtCookieService;

    @PostMapping(ApiPaths.REGISTER)
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
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
        AuthenticationResult authenticationResult = authenticationService.login(loginRequest);
        jwtCookieService.addAccessTokenCookie(authenticationResult.accessToken(), httpServletResponse);
        jwtCookieService.addRefreshTokenCookie(authenticationResult.refreshToken(), httpServletResponse);

        return ResponseEntity.noContent().build();
    }
}
