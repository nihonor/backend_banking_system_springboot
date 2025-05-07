package com.atmSim.atm.controller;

import com.atmSim.atm.entities.User;
import com.atmSim.atm.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/deposit/{id}")
    public User deposit(@PathVariable Long id, @RequestParam double amount) {
        return userService.deposit(id, amount);
    }

    @PutMapping("/withdraw/{id}")
    public User withdraw(@PathVariable Long id, @RequestParam double amount) {
        return userService.withdraw(id, amount);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
