package com.example.webChat.controller;

import com.example.webChat.model.User;
import com.example.webChat.repository.UserRepository;
import com.example.webChat.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@RequestMapping("/v1")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }
    //http://localhost:8085/v1/register kayit yap
    //http://localhost:8085/v1/login login yap

    //register işlem sayfasi
    @GetMapping("/register")
    public ModelAndView showRegistrationForm() {
        return new ModelAndView("register", "user", new User());
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@ModelAttribute User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return new ModelAndView("register", "error", "Username cannot be empty");
        }

        try {
            User registeredUser = userService.registerUser(user);
            return new ModelAndView("login", "success", "User registered successfully ID: " + registeredUser.getId());
        } catch (Exception e) {
            return new ModelAndView("register", "error", "Registration error: " + e.getMessage());
        }
    }


    // Login işlem sayfasi
    @GetMapping("/login")
    public ModelAndView showLoginForm() {
        return new ModelAndView("login");
    }


    @PostMapping("/login")
    public ModelAndView login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        try {
            String token = userService.authenticate(username, password);
            session.setAttribute("username", username); // Kullanıcı adını session'a kaydet
            session.setAttribute("token", token); // Token'ı session'a kaydet
            return new ModelAndView("chat"); // Chat sayfasına yönlendir
        } catch (UsernameNotFoundException e) {
            return new ModelAndView("login", "error", "User not found");
        } catch (IllegalArgumentException e) {
            return new ModelAndView("login", "error", "Invalid credentials");
        }
    }
    @GetMapping("/chat")
    public String showChatPage() {
        return "chat";  // chat.html sayfasını göster
    }
}

/*
package com.example.webChat.controller;

import com.example.webChat.model.User;
import com.example.webChat.repository.UserRepository;
import com.example.webChat.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
@RequestMapping("/v1")
@RestController
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView showRegistrationForm() {
        return new ModelAndView("register", "user", new User());
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be empty");
        }

        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully ID: " + registeredUser.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }





    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        try {
            String token = userService.authenticate(username, password);
            session.setAttribute("token", token);
            System.out.println("Token: " + token);
            return ResponseEntity.ok("success");
        } catch (UsernameNotFoundException e) {
            System.out.println("User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

}*/
