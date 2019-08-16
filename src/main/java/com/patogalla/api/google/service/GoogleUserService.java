package com.patogalla.api.google.service;

import com.patogalla.api.google.config.GoogleConfig;
import com.patogalla.api.google.exception.GoogleException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
public class GoogleUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleUserService.class);

    private final GoogleConfig config;

    @Autowired
    public GoogleUserService(GoogleConfig config) {
        this.config = config;
    }

    public Optional<GoogleIdToken> verifyToken(String token) throws GoogleException {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(config.getClientId()))
                    .build();
            GoogleIdToken result = verifier.verify(token);
            LOGGER.debug("Google user : {}", result);
            return Optional.ofNullable(result);
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.error("Error verifying google user.", e);
            throw new GoogleException(e);
        }
    }


}
