package com.geremere.two_factor_auth;

import com.geremere.two_factor_auth.expection.AuthException;
import com.geremere.two_factor_auth.expection.BaseException;
import com.geremere.two_factor_auth.expection.ExceptionMessage;
import com.geremere.two_factor_auth.service.CustomCacheManager;
import com.geremere.two_factor_auth.storage.InMemorySet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class CustomCacheManagerTest {

    @InjectMocks
    private CustomCacheManager customCacheManager;

    private final String IP = "192.168.0.1";
    private final String CODE = "123456";
    private final String USERNAME = "user1";
    private final String EMAIL = "user1@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Manually set the value of REQUEST_MAX_AMOUNT using reflection
        ReflectionTestUtils.setField(customCacheManager, "REQUEST_MAX_AMOUNT", 5);
        ReflectionTestUtils.setField(customCacheManager, "CLEAN_UP_INTERVAL_IP_CACHE", 5L);
        ReflectionTestUtils.setField(customCacheManager, "CLEAN_UP_INTERVAL_CODE_CACHE", 5L);
        ReflectionTestUtils.setField(customCacheManager, "CLEAN_UP_INTERVAL_AUTH_USERS_CACHE", 5L);
        ReflectionTestUtils.setField(customCacheManager, "REQUEST_LIMIT_CODE_CACHE", 3);
        ReflectionTestUtils.invokeMethod(customCacheManager, "init");
    }

    @Test
    void testPutCodeSuccess() {
        customCacheManager.putCode(USERNAME, CODE);
        String code = customCacheManager.getCode(USERNAME);
        assertEquals(CODE, code);
    }

    @Test
    void testIsAllowedToSendCode() {
        assertTrue(customCacheManager.isAllowedToSendCode(USERNAME));
        customCacheManager.putCode(USERNAME, CODE);
        assertFalse(customCacheManager.isAllowedToSendCode(USERNAME));
    }

    @Test
    void testPutCodeThrowsException() {
        customCacheManager.putCode(USERNAME, CODE);
        BaseException exception = assertThrows(BaseException.class, () -> customCacheManager.putCode(USERNAME, "5678"));
        assertEquals(ExceptionMessage.WAIT_CODE.getValue(), exception.getMessage());
    }


    @Test
    void testRequestAllowed() {
        boolean isAllowed = customCacheManager.request(IP);
        assertTrue(isAllowed);
    }

    @Test
    void testRequestBlockedAfterLimit() throws InterruptedException {

        for (int i = 0; i < 5; i++) {
            assertTrue(customCacheManager.request(IP));
        }
        assertFalse(customCacheManager.request(IP));
    }

    @Test
    void testGetCodeReturnsNullIfNotExists() {
        assertNull(customCacheManager.getCode(USERNAME));
    }

    @Test
    void testAuthUserRemovesCode() {
        customCacheManager.putCode(USERNAME, CODE);
        customCacheManager.authUser(USERNAME);
        assertNull(customCacheManager.getCode(USERNAME));
    }

    @Test
    void testGetRole() {
        assertEquals("ROLE_UNAUTHORIZED", customCacheManager.getRole(USERNAME));
        customCacheManager.putCode(USERNAME, CODE);
        assertEquals("ROLE_PRE_AUTHORIZED", customCacheManager.getRole(USERNAME));
        customCacheManager.authUser(USERNAME);
        assertEquals("ROLE_AUTHORIZED", customCacheManager.getRole(USERNAME));
    }


    @Test
    void testAuthUserRemovesRole() throws InterruptedException {
        ReflectionTestUtils.setField(customCacheManager, "CLEAN_UP_INTERVAL_IP_CACHE", 1L);
        ReflectionTestUtils.setField(customCacheManager, "CLEAN_UP_INTERVAL_CODE_CACHE", 1L);
        ReflectionTestUtils.setField(customCacheManager, "CLEAN_UP_INTERVAL_AUTH_USERS_CACHE", 1L);
        ReflectionTestUtils.invokeMethod(customCacheManager, "init");
        customCacheManager.putCode(USERNAME, CODE);
        customCacheManager.authUser("user2");
        customCacheManager.request(IP);
        Thread.sleep(1500);
        assertTrue(customCacheManager.isAllowedToSendCode(USERNAME));
        assertTrue(customCacheManager.request(IP));
        assertFalse(((InMemorySet<String>) ReflectionTestUtils.getField(customCacheManager, "authorizedUsersCache")).contains("user2"));
    }

    @Test
    void testRequestLimit(){
        customCacheManager.putCode(USERNAME, CODE);
        customCacheManager.getCode(USERNAME);
        customCacheManager.getCode(USERNAME);
        customCacheManager.getCode(USERNAME);
        assertThrows(AuthException.class, () -> customCacheManager.getCode(USERNAME));
    }
}
