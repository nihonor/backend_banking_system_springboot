package com.atmSim.atm.controller;

import com.atmSim.atm.entities.User;
import com.atmSim.atm.service.JwtService;
import com.atmSim.atm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5000")
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private Integer id;
        private String token;
        private String username;
        private String role;
        private String message;

        public LoginResponse() {
            // Default constructor
        }

        public LoginResponse(String token, String username, String role, Integer id) {
            this.id = id;
            this.token = token;
            this.username = username;
            this.role = role;
            this.message = "Login successful";
        }

        // Constructor for error responses
        public static LoginResponse error() {
            LoginResponse response = new LoginResponse();
            response.message = "Invalid credentials";
            return response;
        }
    }

    @Operation(summary = "Register new user", description = "Create a new user account")
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            User validatedUser = userService.validateUser(loginRequest.getUsername(), loginRequest.getPassword());
            String token = jwtService.generateToken(validatedUser.getUsername());

            LoginResponse response = new LoginResponse(
                    token,
                    validatedUser.getUsername(),
                    validatedUser.getRole(), // Assuming your User entity has a role field
                    validatedUser.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return 403 Forbidden for invalid credentials
            return ResponseEntity.status(403).body(LoginResponse.error());
        }
    }

    @Operation(summary = "Logout user", description = "Invalidate user session and JWT token")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(@RequestHeader("Authorization") String token) {
        try {
            // Remove "Bearer " prefix if present
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            // Invalidate the token in JwtService
            jwtService.invalidateToken(jwtToken);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logout successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logout failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
