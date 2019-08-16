package com.patogalla.api.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "patogalla-api.email")
public class EmailConfig {

    private String from = "patogalla@patogalla.com";

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
