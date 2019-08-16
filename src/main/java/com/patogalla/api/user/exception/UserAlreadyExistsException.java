package com.patogalla.api.user.exception;

public class UserAlreadyExistsException extends UserException {

    public UserAlreadyExistsException() {
        super("User already exists.");
    }

    public UserAlreadyExistsException(String msg) {
        super(msg);

    }
}
