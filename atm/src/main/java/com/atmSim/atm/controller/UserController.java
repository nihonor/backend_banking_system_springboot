package com.atmSim.atm.controller;

import com.atmSim.atm.entities.User;
import com.atmSim.atm.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getHello() {
        return "Hello World";
    }

    @GetMapping("/api/users")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }



    @GetMapping("/api/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/api/users/deposit/{id}")
    public User deposit(@PathVariable Long id, @RequestParam double amount) {
        return userService.deposit(id, amount);
    }

    @PutMapping("/api/users/withdraw/{id}")
    public User withdraw(@PathVariable Long id, @RequestParam double amount) {
        return userService.withdraw(id, amount);
    }

    @DeleteMapping("/api/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
