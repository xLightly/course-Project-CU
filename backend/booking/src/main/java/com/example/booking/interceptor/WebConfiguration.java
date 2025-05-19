package com.example.booking.interceptor;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final AuthTokenInterceptor authInterceptor;

    public WebConfiguration(AuthTokenInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        System.out.println("Registering interceptor...");
        registry.addInterceptor(authInterceptor).addPathPatterns("/api/**").excludePathPatterns("/auth/**");
    }
}
