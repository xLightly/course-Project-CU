package com.example.booking.controller;


import com.example.booking.dto.AuthRequest;
import com.example.booking.dto.LoginResponse;
import com.example.grpc.auth.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthServiceClient authClient;
    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody AuthRequest request) {
        System.out.println("Called method");
        if (request.getLogin().equalsIgnoreCase("") || request.getPassword().equalsIgnoreCase("")){
            throw new IllegalArgumentException("Login and password are required");
        }
        long userId = authClient.Register(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthRequest request) {
        String token = authClient.Login(request.getLogin(), request.getPassword());
        long userId = authClient.ValidateToken(token).getUserId();
        String role = authClient.ValidateToken(token).getRole();
        LoginResponse response = new LoginResponse(token, userId, role);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
