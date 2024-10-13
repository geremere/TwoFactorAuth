package com.geremere.two_factor_auth;

import com.geremere.two_factor_auth.expection.AuthException;
import com.geremere.two_factor_auth.expection.BaseException;
import com.geremere.two_factor_auth.expection.ExceptionMessage;
import com.geremere.two_factor_auth.service.AuthService;
import com.geremere.two_factor_auth.service.CustomCacheManager;
import com.geremere.two_factor_auth.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class AuthServiceTest {

    @Mock
    private CustomCacheManager customCacheManager;

    @Mock
    private MailService mailService;

    @InjectMocks
    private AuthService authService;
    private final String CODE = "1234";
    private final String USERNAME = "user1";
    private final String EMAIL = "user1@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mailService.sendSimpleMail(anyString(), anyString())).thenReturn(CODE);
    }

    @Test
    void testSendCodeSuccess() {
        // Mock behavior of customCacheManager and mailService
        when(customCacheManager.isAllowedToSendCode(USERNAME)).thenReturn(true); // Allow code sending
        // Mock code generation

        // Call the method under test
        authService.sendCode(USERNAME, EMAIL);

        // Verify that sendSimpleMail was called with the correct email
        verify(mailService, times(1)).sendSimpleMail(USERNAME, EMAIL);

        // Verify that putCode was called with the correct USERNAME and generated code
        verify(customCacheManager, times(1)).putCode(USERNAME, CODE);
    }

    @Test
    void testSendCodeFailureWhenNotAllowed() {

        // Mock behavior of customCacheManager to not allow sending code
        when(customCacheManager.isAllowedToSendCode(USERNAME)).thenReturn(false);

        // Assert that the BaseException is thrown with the correct message
        BaseException exception = assertThrows(BaseException.class, () -> authService.sendCode(USERNAME, EMAIL));
        assertEquals(ExceptionMessage.WAIT_CODE.getValue(), exception.getMessage());

        // Verify that sendSimpleMail is never called
        verify(mailService, never()).sendSimpleMail(USERNAME, EMAIL);

        // Verify that putCode is never called
        verify(customCacheManager, never()).putCode(anyString(), anyString());
    }

    @Test
    void testVerifyCodeSuccess() {

        // Mock the cache to return the correct code
        when(customCacheManager.getCode(USERNAME)).thenReturn(CODE);

        // Verify no exception is thrown
        assertDoesNotThrow(() -> authService.verifyCode(USERNAME, CODE));

        // Verify authUser is called once
        verify(customCacheManager, times(1)).authUser(USERNAME);
    }

    @Test
    void testVerifyCodeFailureIncorrectCode() {
        String incorrectCode = "5678";

        // Mock the cache to return an incorrect code
        when(customCacheManager.getCode(USERNAME)).thenReturn(incorrectCode);

        // Assert that an AuthException is thrown with the correct message
        assertThrows(AuthException.class, () -> authService.verifyCode(USERNAME, CODE));
    }
}

