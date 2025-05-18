package org.codevoke.probnb.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.codevoke.probnb.dto.RegisterDTO;
import org.codevoke.probnb.dto.UserDTO;
import org.codevoke.probnb.service.RegisterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final RegisterService registerService;

    @PostMapping
    @Operation(
            summary = "register new user",
            description = "create user profile and return new user profile"
    )
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody RegisterDTO registerDTO) {
        logger.info("Register new user: {}", registerDTO.toString());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registerService.register(registerDTO));
    }
}