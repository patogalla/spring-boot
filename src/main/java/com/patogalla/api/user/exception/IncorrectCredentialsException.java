package com.patogalla.api.user.exception;

import com.patogalla.api.utils.model.Identity;

public class IncorrectCredentialsException extends Exception {

    public IncorrectCredentialsException(final Identity id) {
        super(String.format("User {%s} is not found", id));
    }

    public IncorrectCredentialsException(String msg) {
        super(msg);

    }
}
