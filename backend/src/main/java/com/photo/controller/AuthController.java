package com.photo.controller;

import com.photo.dto.AuthResponse;
import com.photo.dto.LoginRequest;
import com.photo.dto.RegisterRequest;
import com.photo.entity.User;
import com.photo.service.UserService;
import com.photo.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token, userDetails.getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(registerRequest.username())) {
                return ResponseEntity.badRequest().body("用户名已存在");
            }

            // 检查邮箱是否已存在（如果提供了邮箱）
            if (registerRequest.email() != null && !registerRequest.email().trim().isEmpty()) {
                if (userService.existsByEmail(registerRequest.email())) {
                    return ResponseEntity.badRequest().body("邮箱已存在");
                }
            }

            // 创建新用户（如果没有提供邮箱，使用默认值）
            String email = registerRequest.email() != null ? registerRequest.email() : registerRequest.username() + "@example.com";
            User user = userService.createUser(
                registerRequest.username(),
                email,
                registerRequest.password()
            );

            // 生成 JWT token
            String token = jwtUtil.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("注册失败: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("未登录");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(userDetails);
    }
} 