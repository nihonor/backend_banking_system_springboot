package com.atmSim.atm.controller;

import com.atmSim.atm.entities.LoginRequest;
import com.atmSim.atm.entities.User;
import com.atmSim.atm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


//
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
//        try {
//            User authenticatedUser = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
//            return ResponseEntity.ok("Login successful for user: " + authenticatedUser.getUsername());
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(401).body("Invalid credentials: " + e.getMessage());
//        }
//    }
}
