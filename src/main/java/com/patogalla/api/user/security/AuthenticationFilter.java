package com.patogalla.api.user.security;

import com.google.common.collect.Lists;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String BEARER_KEY = "Bearer ";

    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public AuthenticationFilter(final AuthenticationManager authenticationManager, final AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse, final FilterChain filterChain) throws ServletException, IOException {
        try {
            LOGGER.debug("Filtering user Auth...");
            String token = Optional.ofNullable(httpRequest.getHeader(HttpHeaders.AUTHORIZATION))
                    .filter((t)->t.contains(BEARER_KEY))
                    .map((t)->{
                        return t.replace(BEARER_KEY, StringUtil.EMPTY_STRING);
                    })
                    .orElseThrow(() -> new InternalAuthenticationServiceException("Unable to authenticate, NO token provided"));
            LOGGER.debug("Token validating: {}", token);
            final Authentication responseAuthentication = authenticationManager.authenticate(new PreAuthenticatedAuthenticationToken(token, null, Lists.newArrayList()));
            SecurityContextHolder.getContext().setAuthentication(responseAuthentication);
            if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
                throw new InternalAuthenticationServiceException("Unable to authenticate with token provided");
            }
            LOGGER.debug("Token validated.");
        } catch (final AuthenticationException e) {
            LOGGER.error("Error auth : {}", e.getMessage());
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(httpRequest, httpResponse, e);
            return;
        }

        filterChain.doFilter(httpRequest, httpResponse);
    }
}

