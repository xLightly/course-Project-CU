package com.example.grpc.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TokenValidationResult {
    private final boolean isValid;
    private final long userId;
    private final String login;
    private final String role;
}
