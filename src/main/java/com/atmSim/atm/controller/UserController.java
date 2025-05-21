package com.atmSim.atm.controller;

import com.atmSim.atm.entities.Account;
import com.atmSim.atm.entities.User;
import com.atmSim.atm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(allowCredentials = "true", origins = { "http://localhost:3000", "http://localhost:5000" })
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Get user accounts")
    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<Account>> getUserAccounts(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserAccounts(id));
    }

    @Operation(summary = "Create new account for user")
    @PostMapping("/{id}/accounts")
    public ResponseEntity<Account> createAccount(@PathVariable Integer id, @RequestParam String accountType) {
        return ResponseEntity.ok(userService.createAccount(id, accountType));
    }
}