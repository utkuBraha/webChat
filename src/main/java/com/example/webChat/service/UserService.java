package com.example.webChat.service;

import com.example.webChat.model.User;
import com.example.webChat.repository.UserRepository;
import com.example.webChat.security.JwtUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // silme, guncelleme, rol ekleme gibi islemler burada yapilabilir.
    public User registerUser(User user) {
        // username and password unique check
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("This username is already taken.");
        }
        // Strong password check
        validatePassword(user.getPassword());
        // password hashing
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // assign a role to user
        user.setRoles(Collections.singleton("USER"));
        // save this user to database
        return userRepository.save(user);
    }


    public String authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        System.out.println(user);
        // username is exist in database
        if (user != null) {
            // Check the password is correct
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Password is correct -> generate token
                String token = jwtUtil.generateToken(username, String.valueOf(user.getId()),user.getRoles());
                System.out.println("User authenticated successfully");
                return token;
            }
        }
        throw new IllegalArgumentException("Invalid credentials");
    }

    private void validatePassword(String password) {
        if (password.length() < 5) {
            throw new RuntimeException("Password at least 5 character.");
        }
    }
}
