package com.patogalla.api.user.service;

import com.patogalla.api.user.config.TokenConfig;
import com.patogalla.api.user.model.Token;
import com.patogalla.api.utils.model.Identity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Objects;
import java.util.Optional;

@Component
public class JwtService {

    private final Key key;

    @Autowired
    public JwtService(final TokenConfig config) {
        this.key = new SecretKeySpec(Objects.requireNonNull(config).jwtSecretKey().getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }

    public String create(final Token token) {
        return Jwts.builder()
                .setSubject(token.getId().stringValue())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public Optional<Identity> parse(final String jwt) {
        return Optional.ofNullable(Jwts.parser()
                        .setSigningKey(key)
                        .parseClaimsJws(jwt)
                        .getBody()
                        .getSubject())
                .map(Identity::unsafeFromString);
    }
}
