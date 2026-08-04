package com.earmarked.authentication.controller;

import com.earmarked.authentication.dto.RegisterRequest;
import com.earmarked.authentication.dto.RegisterResponse;
import com.earmarked.authentication.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest registerRequest) {
        authenticationService.registerUser(registerRequest);
        return ResponseEntity.ok(new RegisterResponse("User registered successfully"));
    }

}
