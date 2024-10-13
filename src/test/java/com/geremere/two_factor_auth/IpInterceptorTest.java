package com.geremere.two_factor_auth;

import com.geremere.two_factor_auth.config.IpInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class IpInterceptorTest {

    @Autowired
    private RequestMappingHandlerMapping mapping;

    @Test
    public void IpInterceptorShouldBeApplied() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/auth/getCode");

        HandlerExecutionChain chain = mapping.getHandler(request);

        assert chain != null;
        Optional<HandlerInterceptor> ipInterceptor = chain.getInterceptorList()
                .stream()
                .filter(IpInterceptor.class::isInstance)
                .findFirst();

        assertTrue(ipInterceptor.isPresent());
    }
}
