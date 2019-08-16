package com.patogalla.api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.flywaydb.core.internal.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenRequestDTO {

    @NotNull
    private String username;

    @NotNull
    @Size(min = 8)
    private String password;

    public TokenRequestDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TokenDto{");
        sb.append("username='").append(username).append('\'');
        sb.append(", password='").append(StringUtils.left(password,1)).append("***\'");
        sb.append('}');
        return sb.toString();
    }
}
