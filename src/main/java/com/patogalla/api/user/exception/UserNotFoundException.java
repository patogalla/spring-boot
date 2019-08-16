package com.patogalla.api.user.exception;

import com.patogalla.api.utils.model.Identity;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(final Identity id) {
        super(String.format("User {%s} is not found", id));
    }

    public UserNotFoundException(String email) {
        super(String.format("User {%s} is not found", email));

    }
}
