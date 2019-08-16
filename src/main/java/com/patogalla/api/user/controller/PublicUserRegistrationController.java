package com.patogalla.api.user.controller;

import com.patogalla.api.google.exception.GoogleException;
import com.patogalla.api.user.dto.GoogleTokenRequestDTO;
import com.patogalla.api.user.dto.UserActivationDTO;
import com.patogalla.api.user.dto.UserDTO;
import com.patogalla.api.user.dto.UserRegistrationDTO;
import com.patogalla.api.user.exception.UserException;
import com.patogalla.api.user.exception.UserNotFoundException;
import com.patogalla.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("public/user/registration")
public class PublicUserRegistrationController {

    private UserService userService;

    @Autowired
    public PublicUserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> startRegistration(@Valid @RequestBody UserRegistrationDTO userDTO) throws UserException {
        return ResponseEntity.ok(userService.startRegistration(userDTO));
    }

    @PostMapping(path = "google")
    public ResponseEntity<UserDTO> startRegistrationGoogle(@Valid @RequestBody GoogleTokenRequestDTO tokenRequestDTO) throws GoogleException, UserException {
        return ResponseEntity.ok(userService.startRegistrationGoogle(tokenRequestDTO));
    }

    @PatchMapping
    public ResponseEntity<UserDTO> checkToken(@Valid @RequestBody UserActivationDTO userDTO) throws UserNotFoundException {
        return ResponseEntity.ok(userService.checkToken(userDTO));
    }

    @PutMapping
    public ResponseEntity<UserDTO> completeRegistration(@Valid @RequestBody UserActivationDTO userDTO) throws UserNotFoundException {
        return ResponseEntity.ok(userService.completeRegistration(userDTO));
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public void handlePME() {
    }

}
