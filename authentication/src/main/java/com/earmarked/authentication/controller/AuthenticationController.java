package com.earmarked.authentication.controller;

import com.earmarked.authentication.config.ApiPaths;
import com.earmarked.authentication.dto.register.RegisterRequest;
import com.earmarked.authentication.dto.register.RegisterResponse;
import com.earmarked.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = ApiPaths.AUTH)
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(ApiPaths.REGISTER)
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        authenticationService.registerUser(registerRequest);
        return ResponseEntity.ok(
                new RegisterResponse("Account created successfully. Please check your inbox for verification."));
    }

    @PostMapping(ApiPaths.ACTIVATE_ACCOUNT)
    public ResponseEntity<Void> activateAccount() {
        return ResponseEntity.noContent().build();
    }
}
