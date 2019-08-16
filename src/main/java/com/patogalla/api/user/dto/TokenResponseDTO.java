package com.patogalla.api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponseDTO {

    private String token;

    public TokenResponseDTO() {
    }

    public TokenResponseDTO(String token) {
        this.token = token;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TokenDto{");
        sb.append(", token='").append(token).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
