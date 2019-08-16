package com.patogalla.api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.flywaydb.core.internal.util.StringUtils;

import javax.validation.constraints.NotNull;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForgotChangePasswordRequestDTO {

    @NotNull
    private String token;
    @NotNull
    private String password;


    public ForgotChangePasswordRequestDTO() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ForgotChangePasswordRequestDTO{");
        sb.append("token='").append(token).append('\'');
        sb.append("password='").append(StringUtils.left(password, 2)).append("***\'");
        sb.append('}');
        return sb.toString();
    }
}
