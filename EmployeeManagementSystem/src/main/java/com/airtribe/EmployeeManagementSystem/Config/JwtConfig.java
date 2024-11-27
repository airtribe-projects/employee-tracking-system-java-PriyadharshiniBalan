package com.airtribe.EmployeeManagementSystem.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtConfig {

    @Value("${auth0.domain}")
    private String auth0Domain;

    @Bean
    public JwtDecoder jwtDecoder() {
        String jwksUri = "https://" + auth0Domain + "/.well-known/jwks.json";
        return NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
    }
}
