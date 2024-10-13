package com.geremere.two_factor_auth.config;

import com.geremere.two_factor_auth.expection.BaseException;
import com.geremere.two_factor_auth.expection.ExceptionMessage;
import com.geremere.two_factor_auth.service.CustomCacheManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class IpInterceptor implements HandlerInterceptor {

    private final CustomCacheManager cacheManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (cacheManager.request(request.getRemoteAddr())) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        } else {
            throw new BaseException(ExceptionMessage.TOO_MUCH_REQUEST);
        }

    }
}
