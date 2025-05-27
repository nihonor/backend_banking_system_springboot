package com.atmSim.atm.controller;

import com.atmSim.atm.entities.Account;
import com.atmSim.atm.entities.Transaction;
import com.atmSim.atm.entities.TransactionDTO;
import com.atmSim.atm.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5000")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transactions", description = "Transaction management APIs")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Deposit money to account", description = "Deposit money using account number")
    @PutMapping("/deposit/{accountNumber}")
    public ResponseEntity<Account> deposit(
            @PathVariable String accountNumber,
            @RequestParam double amount) {
        return ResponseEntity.ok(transactionService.deposit(accountNumber, amount));
    }

    @Operation(summary = "Withdraw money from account", description = "Withdraw money using account number")
    @PutMapping("/withdraw/{accountNumber}")
    public ResponseEntity<Account> withdraw(
            @PathVariable String accountNumber,
            @RequestParam double amount) {
        return ResponseEntity.ok(transactionService.withdraw(accountNumber, amount));
    }

    @Operation(summary = "Transfer money between accounts", description = "Transfer money using account numbers")
    @PutMapping("/transfer")
    public ResponseEntity<Void> transfer(
            @RequestParam String fromAccountNumber,
            @RequestParam String toAccountNumber,
            @RequestParam double amount) {
        transactionService.transfer(fromAccountNumber, toAccountNumber, amount);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get transaction history", description = "Get transaction history for a user")
    @GetMapping("/history/user/{id}")
    public ResponseEntity<List<TransactionDTO>> getUserHistory(@PathVariable Long id) {
        List<Transaction> transactions = transactionService.getAllTransactionsByUserId(id);
        List<TransactionDTO> dtos = transactions.stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
