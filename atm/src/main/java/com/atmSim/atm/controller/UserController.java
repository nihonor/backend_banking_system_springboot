package com.atmSim.atm.controller;

import com.atmSim.atm.entities.Account;
import com.atmSim.atm.entities.User;
import com.atmSim.atm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5000")
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Create new customer (Admin only)")
    @PostMapping("/create-customer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createCustomer(@RequestBody User newUser) {
        return ResponseEntity.ok(userService.createCustomer(newUser));
    }

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

    @Operation(summary = "Edit user details (Admin only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> editUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.editUser(id, updatedUser));
    }

    @Operation(summary = "Suspend user (Admin only)")
    @PutMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> suspendUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.suspendUser(id));
    }

    @Operation(summary = "Activate user (Admin only)")
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> activateUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }
}
