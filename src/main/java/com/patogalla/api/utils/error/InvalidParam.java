package com.patogalla.api.utils.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvalidParam {

    @NotNull
    private final String param;

    @NotNull
    private final String code;

    private final String reason;

    public InvalidParam(@NotNull String param, @NotNull String code, String reason) {
        this.param = param;
        this.code = code;
        this.reason = reason;
    }

    public String getParam() {
        return param;
    }

    public String getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }
}
