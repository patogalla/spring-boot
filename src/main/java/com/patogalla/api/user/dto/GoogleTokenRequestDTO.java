package com.patogalla.api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleTokenRequestDTO {

    @NotNull
    private String token;

    public GoogleTokenRequestDTO() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GoogleTokenRequestDTO{");
        sb.append("token='").append(token).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
