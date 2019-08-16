package com.patogalla.api.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "patogalla-api.user.token")
public class TokenConfig {

    private String jwtSecretKey;
    private int tokenTtlDays = 7;
    private int activationTokenTtlDays = 1;
    private int tokenCacheTtlSeconds = 60;
    private long tokenCacheMaxSize = 1000;

    public Duration tokenTtlDays() {
        return Duration.ofDays(tokenTtlDays);
    }

    public Duration tokenCacheTtlSeconds() {
        return Duration.ofSeconds(tokenCacheTtlSeconds);
    }

    public long tokenCacheMaxSize() {
        return tokenCacheMaxSize;
    }

    public String jwtSecretKey() {
        return jwtSecretKey;
    }

    public String getJwtSecretKey() {
        return jwtSecretKey;
    }

    public void setJwtSecretKey(String jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    public int getTokenTtlDays() {
        return tokenTtlDays;
    }

    public void setTokenTtlDays(int tokenTtlDays) {
        this.tokenTtlDays = tokenTtlDays;
    }

    public int getTokenCacheTtlSeconds() {
        return tokenCacheTtlSeconds;
    }

    public void setTokenCacheTtlSeconds(int tokenCacheTtlSeconds) {
        this.tokenCacheTtlSeconds = tokenCacheTtlSeconds;
    }

    public long getTokenCacheMaxSize() {
        return tokenCacheMaxSize;
    }

    public void setTokenCacheMaxSize(long tokenCacheMaxSize) {
        this.tokenCacheMaxSize = tokenCacheMaxSize;
    }

    public Duration activationTokenTtlDays() {
        return Duration.ofDays(activationTokenTtlDays);
    }

    public int getActivationTokenTtlDays() {
        return activationTokenTtlDays;
    }

    public void setActivationTokenTtlDays(int activationTokenTtlDays) {
        this.activationTokenTtlDays = activationTokenTtlDays;
    }
}
