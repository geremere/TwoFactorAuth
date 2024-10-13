package com.geremere.two_factor_auth.expection;

public class AuthException extends RuntimeException {
    public AuthException(ExceptionMessage message) {
        super(message.getValue());
    }
}
