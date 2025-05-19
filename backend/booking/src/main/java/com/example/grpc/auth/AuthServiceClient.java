package com.example.grpc.auth;

import com.example.booking.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class AuthServiceClient implements AuthServicClientInterface {

    private final ManagedChannel channel;
    private final AuthServiceGrpc.AuthServiceBlockingStub stub;

    public AuthServiceClient() {
        String host = System.getenv().getOrDefault("AUTH_SERVICE_HOST", "auth-service");
        int port = Integer.parseInt(System.getenv().getOrDefault("AUTH_SERVICE_PORT", "50051"));

        System.out.println("Connecting to auth service: " + host + ":" + port);

        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        this.stub = AuthServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void Close() {
        channel.shutdown();
    }

    @Override
    public long Register(String login, String password) {
        try {
            RegisterRequest req = RegisterRequest.newBuilder().setLogin(login).setPassword(password).build();
            return stub.register(req).getUserId();
        } catch (StatusRuntimeException e) {
            handleGrpcError("Register", e);
            throw e;
        }
    }

    @Override
    public String Login(String login, String password) {
        try {
            LoginRequest req = LoginRequest.newBuilder().setLogin(login).setPassword(password).build();
            return stub.login(req).getToken();
        } catch (StatusRuntimeException e) {
            handleGrpcError("Login", e);
            throw e;
        }
    }

    @Override
    public TokenValidationResult ValidateToken(String token) {
        try {
            ValidateTokenRequest req = ValidateTokenRequest.newBuilder().setToken(token).build();
            ValidateTokenResponse res = stub.validateToken(req);
            return new TokenValidationResult(res.getValid(), res.getUserId(), res.getLogin(), res.getRole());
        } catch (StatusRuntimeException e) {
            handleGrpcError("ValidateToken", e);
            return new TokenValidationResult(false, -1, "", "");
        }
    }

    @Override
    public Boolean PromoteToAdmin(String login) {
        try {
            PromoteRequest req = PromoteRequest.newBuilder()
                .setTargetLogin(login)
                .build();
            return stub.promoteToAdmin(req).getSuccess();
        } catch (StatusRuntimeException e) {
            handleGrpcError("PromoteToAdmin", e);
            return false;
        }
    }

    private void handleGrpcError(String method, StatusRuntimeException e) {
        System.err.println("gRPC error during " + method + ": " + e.getStatus().getCode() + " - " + e.getMessage());
    }

    @Override
    public List<String>GetUsers(){
        try{
            Empty req = Empty.newBuilder().build();
            return stub.getUsers(req).getLoginsList();
        } catch (StatusRuntimeException e) {
            handleGrpcError("GetUsers", e);
            return null;
        }
    }
}
