package com.example.grpc.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {
    private final AuthServiceClient authClient;


    public TokenValidationResult validate(String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new RuntimeException("invalid authHeader");
        }
        String token = authHeader.substring(7);
        TokenValidationResult tokenValidationResult = authClient.ValidateToken(token);
        if (!tokenValidationResult.isValid()){
            throw new RuntimeException("Invalid access token");
        }
        return tokenValidationResult;
    }
    
}
