package com.patogalla.api.user.security;

import com.patogalla.api.user.dto.UserDTO;
import com.patogalla.api.user.exception.UserNotFoundException;
import com.patogalla.api.user.service.TokenService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class TokenAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationProvider.class);

    private final TokenService tokenService;

    @Autowired
    public TokenAuthenticationProvider(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String jwt = readToken(authentication);
        try {
            UserDTO userDTO = tokenService.verifyLoginToken(jwt);
            LOGGER.debug("Setting user : {}", userDTO);
            return createAuthentication(userDTO);
        } catch (UserNotFoundException | ExecutionException e) {
            throw new AuthenticationCredentialsNotFoundException(e.getMessage(), e);
        }
    }

    private Authentication createAuthentication(UserDTO userDTO) {
        AbstractAuthenticationToken result = new UsernamePasswordAuthenticationToken(userDTO, null, Lists.newArrayList());
        result.setDetails(userDTO);
        return result;
    }

    protected String readToken(Authentication authentication) {
        LOGGER.debug("Reading auth data.");
        return Optional
                .of(authentication.getPrincipal())
                .filter(principal -> principal instanceof String)
                .map(principal -> (String) principal)
                .filter(principal -> !principal.isEmpty())
                .orElseThrow(() -> new BadCredentialsException("Invalid token"));
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}

