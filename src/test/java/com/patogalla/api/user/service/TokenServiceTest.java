package com.patogalla.api.user.service;

import com.patogalla.api.CooknowApiApplicationTests;
import com.patogalla.api.user.exception.UserNotFoundException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TokenServiceTest extends CooknowApiApplicationTests {

    @Autowired
    private TokenService tokenService;

    @Test
    public void test() throws UserNotFoundException {
        this.tokenService.verifyForgotToken("");
    }
}
