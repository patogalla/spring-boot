package com.patogalla.api.user.controller;

import com.patogalla.api.google.exception.GoogleException;
import com.patogalla.api.user.dto.*;
import com.patogalla.api.user.exception.IncorrectCredentialsException;
import com.patogalla.api.user.exception.UserNotFoundException;
import com.patogalla.api.user.service.TokenService;
import com.patogalla.api.user.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("public/user/login")
@Api(value = "PublicUserLoginController")
public class PublicUserLoginController {

    private TokenService tokenService;

    @Autowired
    public PublicUserLoginController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody TokenRequestDTO tokenDTO) throws IncorrectCredentialsException {
        return ResponseEntity.ok(tokenService.createUserToken(tokenDTO));
    }

    @PostMapping(path = "google")
    public ResponseEntity<TokenResponseDTO> loginGoogle(@Valid @RequestBody GoogleTokenRequestDTO tokenDTO) throws UserNotFoundException, IncorrectCredentialsException, GoogleException {
        return ResponseEntity.ok(tokenService.createUserGoogleToken(tokenDTO));
    }

    @PostMapping(path = "forgot")
    public ResponseEntity<?> forgot(@Valid @RequestBody ForgotRequestDTO tokenDTO) throws UserNotFoundException {
        tokenService.createUserForgotToken(tokenDTO);
        return ResponseEntity.ok(tokenDTO);
    }

    @GetMapping(path = "forgot/{token}")
    public ResponseEntity<UserDTO> getForgot(@PathVariable String token) throws UserNotFoundException {
        UserDTO userDTO = tokenService.verifyForgotToken(token);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping(path = "forgot")
    public ResponseEntity<UserDTO> changePassword(@Valid @RequestBody ForgotChangePasswordRequestDTO passwordRequestDTO) throws UserNotFoundException {
        return ResponseEntity.ok(tokenService.changePassword(passwordRequestDTO));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UserNotFoundException.class)
    public void handleUNFE() {
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(IncorrectCredentialsException.class)
    public void handleICE() {
    }

}
