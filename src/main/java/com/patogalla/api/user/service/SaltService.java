package com.patogalla.api.user.service;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Service
public class SaltService implements Serializable {
    private static final int KEY_LENGTH = 64;

    public static String random() {
        return String.valueOf(Hex.encode(KeyGenerators.secureRandom(KEY_LENGTH).generateKey()));
    }

    public String apply(final String salt, final String rawPassword) {
        return createEncoder(salt).encode(Objects.requireNonNull(rawPassword));
    }

    public boolean check(final String salt, final String saltyPassword, final String rawPassword) {
        return isHex(saltyPassword) && createEncoder(salt).matches(rawPassword, saltyPassword);
    }

    private boolean isHex(final String candidate) {
        return Optional.ofNullable(Hex.decode(candidate)).isPresent();
    }

    private PasswordEncoder createEncoder(String salt) {
        return new Pbkdf2PasswordEncoder(salt);
    }

}
