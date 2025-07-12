package org.codevoke.probnb.controllers;

import jakarta.validation.Valid;
import org.codevoke.probnb.dto.LoginDTO;
import org.codevoke.probnb.service.LoginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<Map<String, String>> login (@Valid @RequestBody LoginDTO loginDTO) {
        logger.info("login user, {}", loginDTO.toString());
        return ResponseEntity.ok(loginService.login(loginDTO));
    }
}
