package com.atmSim.atm.service;

import com.atmSim.atm.entities.Account;
import com.atmSim.atm.entities.Transaction;
import com.atmSim.atm.entities.User;
import com.atmSim.atm.repositories.AccountRepository;
import com.atmSim.atm.repositories.TransactionRepository;
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

    @Transactional
    public Account deposit(String accountNumber, double amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance() + amount);
        Account savedAccount = accountRepository.save(account);
        recordTransaction("DEPOSIT", amount, null, savedAccount, savedAccount.getUser());
        return savedAccount;
    }

    @Transactional
    public Account withdraw(String accountNumber, double amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            Account savedAccount = accountRepository.save(account);
            recordTransaction("WITHDRAW", amount, savedAccount, null, savedAccount.getUser());
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

            recordTransaction("TRANSFER", amount, fromAccount, toAccount, fromAccount.getUser());
        } else {
            throw new RuntimeException("Insufficient balance");
        }
    }

    private void recordTransaction(String type, double amount, Account fromAccount, Account toAccount, User user) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setUser(user);
        transaction.setTimestamp(Instant.now());
        transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
}
