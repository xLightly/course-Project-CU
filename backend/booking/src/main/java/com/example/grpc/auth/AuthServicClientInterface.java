package com.example.grpc.auth;

import java.util.List;

public interface AuthServicClientInterface {

    void Close();

    long Register(String login, String password);

    String Login(String login, String password);

    TokenValidationResult ValidateToken(String token);

    Boolean PromoteToAdmin(String targetLogin);

    List<String> GetUsers();

} 
