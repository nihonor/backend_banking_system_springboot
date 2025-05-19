package com.atmSim.atm.service;

import com.atmSim.atm.entities.Account;
import com.atmSim.atm.entities.User;
import com.atmSim.atm.repositories.AccountRepository;
import com.atmSim.atm.repositories.UserRepository;
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

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

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
        accountRepository.deleteById(id);
    }

    // User registration
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        user.setRole("USER"); // Set default role
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
}
