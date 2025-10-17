package com.bar.stormhook.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import java.time.Duration;

@Configuration
class JwtValidationConfig {

    @Bean
    JwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
            @Value("${stormhook.jwt.expected-issuer}") String iss,
            @Value("${stormhook.jwt.expected-audience}") String aud) {

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        decoder.setJwtValidator(validator(iss, aud));
        return decoder;
    }

    private OAuth2TokenValidator<Jwt> validator(String iss, String aud) {
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(iss);
        OAuth2TokenValidator<Jwt> audience = token ->
                token.getAudience().contains(aud)
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","bad audience",""));
        return new DelegatingOAuth2TokenValidator<>(withIssuer, audience);
    }
}
