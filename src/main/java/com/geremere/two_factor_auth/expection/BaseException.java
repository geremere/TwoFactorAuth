package com.geremere.two_factor_auth.expection;

public class BaseException extends RuntimeException {
    public BaseException(ExceptionMessage message) {
        super(message.getValue());
    }

}
