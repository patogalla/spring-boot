package com.patogalla.api.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForgotRequestDTO {

    @NotNull
    private String email;
    @NotNull
    private String callbackUrl;


    public ForgotRequestDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ForgotRequestDTO{");
        sb.append("email='").append(email).append('\'');
        sb.append("callbackUrl='").append(callbackUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
