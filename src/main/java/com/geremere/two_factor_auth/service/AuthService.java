package com.geremere.two_factor_auth.service;

import com.geremere.two_factor_auth.expection.AuthException;
import com.geremere.two_factor_auth.expection.BaseException;
import com.geremere.two_factor_auth.expection.ExceptionMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomCacheManager customCacheManager;
    private final MailService mailService;

    public void sendCode(String username, String email) {
        if(customCacheManager.isAllowedToSendCode(username)) {
            customCacheManager.putCode(username, mailService.sendSimpleMail(username, email));
        } else {
            throw new BaseException(ExceptionMessage.WAIT_CODE);
        }
    }

    public void verifyCode(String username, String code) {
        if (code.equals(customCacheManager.getCode(username))) {
            customCacheManager.authUser(username);
        } else {
            throw new AuthException(ExceptionMessage.INCORRECT_CODE);
        }
    }
}
