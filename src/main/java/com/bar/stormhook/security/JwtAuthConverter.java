package com.bar.stormhook.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        var scopes = Optional.ofNullable(jwt.getClaimAsString("scope"))
                .map(s -> Arrays.asList(s.split("\\s+")))
                .orElseGet(List::of);
        var authorities = scopes.stream()
                .map(s -> new SimpleGrantedAuthority("SCOPE_" + s))
                .collect(Collectors.toSet());
        // stash tenant_id for downstream use
        var details = Map.of("tenant_id", jwt.getClaimAsString("tenant_id"));
        return new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(
                jwt, authorities, jwt.getSubject()) {{
            setDetails(details);
        }};
    }
}
