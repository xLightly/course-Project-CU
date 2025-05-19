package com.example.booking.interceptor;

import com.example.grpc.auth.AuthServiceClient;
import com.example.grpc.auth.TokenValidationResult;
import io.grpc.StatusRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.logging.LogRecord;

@Component
public class AuthTokenInterceptor implements HandlerInterceptor {
    private final AuthServiceClient authServiceClient;

    public AuthTokenInterceptor(AuthServiceClient client) {
        this.authServiceClient = client;
        System.out.println("AuthTokenInterceptor created");
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Handler type: " + handler.getClass().getName());
        if (request.getRequestURI().startsWith("/auth/")) {
            return true;
        }
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader ==null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing auth header");
            return false;
        }
        String token = authHeader.substring(7);
        TokenValidationResult tokenValidationResult;
        try{
            tokenValidationResult = authServiceClient.ValidateToken(token);
        } catch(StatusRuntimeException e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token validation failed");
            return false;
        }
        if (!tokenValidationResult.isValid()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "invalid token");
            return false;
        }
        RequireRole role = method.getMethodAnnotation(RequireRole.class);
        if (role == null){
            role = method.getBeanType().getAnnotation(RequireRole.class);
        }
        if (role != null){
            String requireRile = role.value();
            if (!requireRile.equalsIgnoreCase(tokenValidationResult.getRole())){
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "permission denied");
                return false;
            };
        }
        request.setAttribute("userId", tokenValidationResult.getUserId());
        request.setAttribute("login", tokenValidationResult.getLogin());
        request.setAttribute("role", tokenValidationResult.getRole());
        return true;
    }
}
