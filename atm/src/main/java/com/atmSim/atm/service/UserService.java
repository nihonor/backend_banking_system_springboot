package com.atmSim.atm.service;

import com.atmSim.atm.entities.User;
import com.atmSim.atm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
    public User createUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user); // Uncomment and fix this line
    }
    public User deposit(Long id, double amount) {
        User user = getUserById(id);
        user.setBalance(user.getBalance() + amount);
        return userRepository.save(user);
    }

    public User withdraw(Long id, double amount) {
        User user = getUserById(id);
        if (user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
        } else {
            throw new RuntimeException("Insufficient balance");
        }
        User updatedUser = userRepository.save(user);
        try {
            emailService.sendWithdrawalEmail(user.getEmail(), amount, user.getBalance());
        } catch (Exception e) {
            System.err.println("Failed to send withdrawal email: " + e.getMessage());
        }
        return updatedUser;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}