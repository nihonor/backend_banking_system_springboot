package com.atmSim.atm.service;

import com.atmSim.atm.entities.Account;
import com.atmSim.atm.entities.Transaction;
import com.atmSim.atm.entities.User;
import com.atmSim.atm.repositories.AccountRepository;
import com.atmSim.atm.repositories.TransactionRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    @Transactional
    public Account deposit(String accountNumber, double amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance() + amount);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = recordTransaction("DEPOSIT", amount, null, savedAccount, savedAccount.getUser());

        try {
            emailService.sendTransactionNotification(
                    savedAccount.getUser().getEmail(),
                    "DEPOSIT",
                    amount,
                    savedAccount.getBalance(),
                    null,
                    savedAccount.getAccountNumber());
        } catch (MessagingException e) {
            // Log the error but don't stop the transaction
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        return savedAccount;
    }

    @Transactional
    public Account withdraw(String accountNumber, double amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            Account savedAccount = accountRepository.save(account);

            Transaction transaction = recordTransaction("WITHDRAW", amount, savedAccount, null, savedAccount.getUser());

            try {
                emailService.sendTransactionNotification(
                        savedAccount.getUser().getEmail(),
                        "WITHDRAW",
                        amount,
                        savedAccount.getBalance(),
                        savedAccount.getAccountNumber(),
                        null);
            } catch (MessagingException e) {
                System.err.println("Failed to send email notification: " + e.getMessage());
            }

            return savedAccount;
        } else {
            throw new RuntimeException("Insufficient balance");
        }
    }

    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("Source account not found"));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (fromAccount.getBalance() >= amount) {
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + amount);

            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            Transaction transaction = recordTransaction("TRANSFER", amount, fromAccount, toAccount,
                    fromAccount.getUser());

            // Send notification to sender
            try {
                emailService.sendTransactionNotification(
                        fromAccount.getUser().getEmail(),
                        "TRANSFER",
                        amount,
                        fromAccount.getBalance(),
                        fromAccount.getAccountNumber(),
                        toAccount.getAccountNumber());
            } catch (MessagingException e) {
                System.err.println("Failed to send email notification to sender: " + e.getMessage());
            }

            // Send notification to receiver
            try {
                emailService.sendTransactionNotification(
                        toAccount.getUser().getEmail(),
                        "TRANSFER",
                        amount,
                        toAccount.getBalance(),
                        fromAccount.getAccountNumber(),
                        toAccount.getAccountNumber());
            } catch (MessagingException e) {
                System.err.println("Failed to send email notification to receiver: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Insufficient balance");
        }
    }

    private Transaction recordTransaction(String type, double amount, Account fromAccount, Account toAccount,
            User user) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setUser(user);
        transaction.setTimestamp(Instant.now());
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
}
