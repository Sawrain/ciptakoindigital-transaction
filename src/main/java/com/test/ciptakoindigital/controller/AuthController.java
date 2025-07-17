package com.test.ciptakoindigital.controller;

import com.test.ciptakoindigital.dto.AuthResponse;
import com.test.ciptakoindigital.dto.LoginRequest;
import com.test.ciptakoindigital.dto.RegisterRequest;
import com.test.ciptakoindigital.entity.User;
import com.test.ciptakoindigital.repository.UserRepository;
import com.test.ciptakoindigital.security.JwtUtil;
import com.test.ciptakoindigital.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        String token = authService.login(new LoginRequest(request.getUsername(), request.getPassword()));
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }

//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody User user) {
//        if (userRepo.findByUsername(user.getUsername()) != null) {
//            return ResponseEntity.ok(response(false, "Username already exists", null));
//        }
//        user.setPassword(encoder.encode(user.getPassword()));
//        userRepo.save(user);
//        return ResponseEntity.ok(response(true, "Register success", null));
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody User user) {
//        User found = userRepo.findByUsername(user.getUsername());
//        if (found == null || !encoder.matches(user.getPassword(), found.getPassword())) {
//            return ResponseEntity.status(401).body(response(false, "Invalid credentials", null));
//        }
//        String token = jwtUtil.generateToken(found.getUsername());
//        return ResponseEntity.ok(response(true, "Login success", Map.of("token", token)));
//    }
//
//    private Map<String, Object> response(boolean success, String message, Object data) {
//        return Map.of("success", success, "message", message, "data", data);
//    }
}
