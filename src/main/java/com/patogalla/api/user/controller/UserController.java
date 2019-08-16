package com.patogalla.api.user.controller;

import com.patogalla.api.user.exception.UserNotFoundException;
import com.patogalla.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@RestController
@RequestMapping("user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Logic on : {@link com.patogalla.api.user.security.TokenAuthenticationProvider}
     * @param principal
     * @return {@link com.patogalla.api.user.dto.UserDTO}
     */
    @GetMapping
    public ResponseEntity<?> get(@ApiIgnore Authentication principal) {
        return ResponseEntity.ok(principal.getDetails());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public void handlePME() {
    }

}
