package com.atmSim.atm.service;

import com.atmSim.atm.entities.Account;
import com.atmSim.atm.entities.User;
import com.atmSim.atm.repositories.AccountRepository;
import com.atmSim.atm.repositories.UserRepository;
import com.atmSim.atm.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Don't allow suspended users to authenticate
        if ("SUSPENDED".equals(user.getStatus())) {
            throw new UsernameNotFoundException("User account is suspended");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<Account> getUserAccounts(Integer userId) {
        User user = getUserById(userId);
        return user.getAccounts();
    }

    public Account createAccount(Integer userId, String accountType) {
        User user = getUserById(userId);
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(accountType);
        account.setBalance(0.0);
        account.setUser(user);
        user.getAccounts().add(account);
        userRepository.save(user);
        return account;
    }

    private String generateAccountNumber() {
        // Simple implementation - replace with a more robust solution
        return "ACC" + System.currentTimeMillis();
    }

    // Admin methods
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Delete all transactions where this account is either the source or
        // destination
        transactionRepository.deleteByFromAccountOrToAccount(account, account);

        // Now we can safely delete the account
        accountRepository.delete(account);
    }

    // User registration
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        user.setRole("USER"); // Set default role
        user.setStatus("ACTIVE"); // Set default status
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password
        return userRepository.save(user);
    }

    // Role validation
    public boolean isAdmin(Integer userId) {
        User user = getUserById(userId);
        return "ADMIN".equals(user.getRole());
    }

    // Login validation
    public User validateUser(String username, String password) {
        try {
            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } catch (Exception e) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public User suspendUser(Integer id) {
        User user = getUserById(id);
        user.setStatus("SUSPENDED");
        return userRepository.save(user);
    }

    public User activateUser(Integer id) {
        User user = getUserById(id);
        user.setStatus("ACTIVE");
        return userRepository.save(user);
    }

    public User editUser(Integer id, User updatedUser) {
        User existingUser = getUserById(id);

        // Update fields if they are provided in the request
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
            // Check if new username is already taken by another user
            userRepository.findByUsername(updatedUser.getUsername())
                    .ifPresent(user -> {
                        if (!user.getId().equals(id)) {
                            throw new RuntimeException("Username already exists");
                        }
                    });
            existingUser.setUsername(updatedUser.getUsername());
        }

        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Only allow admin to change roles
        if (updatedUser.getRole() != null && !updatedUser.getRole().isEmpty()) {
            existingUser.setRole(updatedUser.getRole());
        }

        return userRepository.save(existingUser);
    }

    public User createCustomer(User newUser) {
        // Validate required fields
        if (newUser.getUsername() == null || newUser.getUsername().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (newUser.getPassword() == null || newUser.getPassword().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        if (newUser.getEmail() == null || newUser.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        // Check if username already exists
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Set default values and encode password
        newUser.setRole("USER"); // Default role is USER
        newUser.setStatus("ACTIVE"); // Default status is ACTIVE
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        // Save and return the new user
        User savedUser = userRepository.save(newUser);

        // Send welcome email
        try {
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getUsername());
        } catch (Exception e) {
            // Log the error but don't stop the user creation
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        return savedUser;
    }

    public Account editAccount(Long id, Account updatedAccount) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Update fields if they are provided in the request
        if (updatedAccount.getAccountType() != null && !updatedAccount.getAccountType().isEmpty()) {
            existingAccount.setAccountType(updatedAccount.getAccountType());
        }

        if (updatedAccount.getAccountNumber() != null && !updatedAccount.getAccountNumber().isEmpty()) {
            // Check if new account number is already taken by another account
            accountRepository.findByAccountNumber(updatedAccount.getAccountNumber())
                    .ifPresent(account -> {
                        if (!account.getId().equals(id)) {
                            throw new RuntimeException("Account number already exists");
                        }
                    });
            existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
        }

        if (updatedAccount.getBalance() != null) {
            existingAccount.setBalance(updatedAccount.getBalance());
        }

        // If user ID is provided and different from current user, transfer account
        // ownership
        if (updatedAccount.getUser() != null && updatedAccount.getUser().getId() != null &&
                !updatedAccount.getUser().getId().equals(existingAccount.getUser().getId())) {
            User newOwner = getUserById(updatedAccount.getUser().getId());
            existingAccount.setUser(newOwner);
        }

        return accountRepository.save(existingAccount);
    }

    public Account createAccountForUser(Integer userId, String accountType, Double initialBalance) {
        User user = getUserById(userId);

        // Validate account type
        if (!isValidAccountType(accountType)) {
            throw new RuntimeException("Invalid account type. Allowed types are: CHECKING, SAVINGS");
        }

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(accountType.toUpperCase());
        account.setBalance(initialBalance);
        account.setUser(user);

        // Add the account to user's accounts list
        if (user.getAccounts() == null) {
            user.setAccounts(new ArrayList<>());
        }
        user.getAccounts().add(account);

        // Save the user which will cascade save the account
        userRepository.save(user);

        return account;
    }

    private boolean isValidAccountType(String accountType) {
        if (accountType == null)
            return false;
        String type = accountType.toUpperCase();
        return type.equals("CHECKING") || type.equals("SAVINGS");
    }
}
