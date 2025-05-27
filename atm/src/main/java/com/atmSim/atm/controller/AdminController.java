package com.atmSim.atm.controller;

import com.atmSim.atm.entities.Account;
import com.atmSim.atm.entities.Transaction;
import com.atmSim.atm.entities.User;
import com.atmSim.atm.service.TransactionService;
import com.atmSim.atm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5000")
@AllArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserService userService;
    private final TransactionService transactionService;

    @Operation(summary = "Get all users")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @Operation(summary = "Get all accounts")
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(userService.findAllAccounts());
    }

    @Operation(summary = "Delete user by ID")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete account by ID")
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        userService.deleteAccount(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all transactions")
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @Operation(summary = "Suspend user account")
    @PutMapping("/users/{id}/suspend")
    public ResponseEntity<User> suspendUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.suspendUser(id));
    }

    @Operation(summary = "Activate user account")
    @PutMapping("/users/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @Operation(summary = "Edit user details")
    @PutMapping("/users/{id}")
    public ResponseEntity<User> editUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.editUser(id, updatedUser));
    }

    @Operation(summary = "Edit account details")
    @PutMapping("/accounts/{id}")
    public ResponseEntity<Account> editAccount(@PathVariable Long id, @RequestBody Account updatedAccount) {
        return ResponseEntity.ok(userService.editAccount(id, updatedAccount));
    }

    @Operation(summary = "Create account for user")
    @PostMapping("/users/{userId}/accounts")
    public ResponseEntity<Account> createAccountForUser(
            @PathVariable Integer userId,
            @RequestParam String accountType,
            @RequestParam(required = false, defaultValue = "0.0") Double initialBalance) {
        return ResponseEntity.ok(userService.createAccountForUser(userId, accountType, initialBalance));
    }
}