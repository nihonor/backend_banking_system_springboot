package com.atmSim.atm.controller;

import com.atmSim.atm.entities.Transaction;
import com.atmSim.atm.entities.TransactionDTO;
import com.atmSim.atm.entities.User;
import com.atmSim.atm.service.TransactionService;
import com.atmSim.atm.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final UserService userService;
    private final TransactionService transactionService;


    @PutMapping("/deposit/{id}")
    public ResponseEntity<User> deposit(@PathVariable Long id, @RequestParam double amount) {
        return ResponseEntity.ok(userService.deposit(id, amount));
    }

    @PutMapping("/withdraw/{id}")
    public ResponseEntity<?> withdraw(@PathVariable Long id, @RequestParam double amount) {
        try {
            return ResponseEntity.ok(userService.withdraw(id, amount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/balance/{id}")
    public ResponseEntity<Double> getBalance(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user.getBalance());
    }

    @GetMapping("/history/user/{id}")
    public ResponseEntity<List<TransactionDTO>> getUserHistory(@PathVariable Long id) {
        List<Transaction> transactions = transactionService.getAllTransactionsByUserId(id);
        List<TransactionDTO> dtos = transactions.stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
