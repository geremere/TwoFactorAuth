package com.geremere.two_factor_auth.expection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionMessage {
    INTERNAL_SERVER_ERROR("Something went wrong"),
    INCORRECT_CODE("incorrect code"),
    WAIT_CODE("please wait 5 minutes until next code request"),
    CODE_CHECK_AMOUNT_EXCEED("please wait 5 minutes, amount of code check request was exceeded"),
    INCORRECT_TOKEN("incorrect jwt token"),
    TOO_MUCH_REQUEST("Too much requests, please wait 5 minutes and try again");

    private final String value;
}
