package com.patogalla.api.user.controller;

import com.patogalla.api.user.dto.UserDTO;
import com.patogalla.api.user.exception.UserException;
import com.patogalla.api.user.exception.UserNotFoundException;
import com.patogalla.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("admin/user")
public class AdminUserController {

    private UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> get() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserDTO> save(@Valid @RequestBody UserDTO userDTO) throws UserException, UserNotFoundException {
        return ResponseEntity.ok(userService.save(userDTO));
    }
}
