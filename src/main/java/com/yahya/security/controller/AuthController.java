package com.yahya.security.controller;

import com.yahya.security.dto.AuthRequest;
import com.yahya.security.dto.AuthResponse;
import com.yahya.security.model.Users;
import com.yahya.security.repo.UserRepository;
import com.yahya.security.security.JwtUtil;
import com.yahya.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            // Authenticate User
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            if (!authentication.isAuthenticated()) throw new Exception("Incorrect username or password");
            // Store the authentication object in the SecurityContextHolder
//            SecurityContextHolder.getContext().getAuthentication(authentication);

            // Generate JWT
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            System.out.println(userDetails.getUsername());
            System.out.println(userDetails.getPassword());

            final String jwt = jwtUtil.generateToken(userDetails);
            System.out.println("JWT: " + jwt);

            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody Users users) throws Exception {
        try {
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            userRepository.save(users);
            return ResponseEntity.ok("User registered");

        } catch (Exception exception) {
            throw new Exception("Failed", exception);
        }
    }
}