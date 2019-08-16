package com.patogalla.api.google.exception;

public class GoogleException extends Exception {
    public GoogleException(String s) {
        super(s);
    }

    public GoogleException(Throwable throwable) {
        super(throwable);
    }
}
