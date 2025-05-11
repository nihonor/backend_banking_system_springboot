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

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Method to find all users
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // Method to get a user by ID
    public User getUserById(Long id) {
        return userRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Method to create a new user
    public User createUser(User user) {
//        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

    // Method to deposit money
    public User deposit(Long id, double amount) {
        User user = getUserById(id);
        user.setBalance(user.getBalance() + amount);
        return userRepository.save(user);
    }

    // Method to withdraw money
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

    // Method to delete a user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(Math.toIntExact(id));
    }

//    // Method to authenticate a user by username and password
//    public User authenticateUser(String email, String password) {
//        // First find by email only
//        User user = userRepository.findByEmail(email);
//
//        if (user == null) {
//            throw new RuntimeException("User not found with email: " + email);
//        }
//
//        // Then verify the password
//        if (passwordEncoder.matches(password, user.getPassword())) {
//            return user;
//        } else {
//            throw new RuntimeException("Invalid password");
//        }
//
//    }
}
