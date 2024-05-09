package com.example.webChat.controller;

import com.example.webChat.dto.MessageDTO;
import com.example.webChat.model.Message;
import com.example.webChat.model.User;
import com.example.webChat.repository.UserRepository;
import com.example.webChat.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    public MessageController(MessageService messageService, UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO messageDTO) {
        Optional<User> sender = userRepository.findByUsername(messageDTO.getSenderUsername());
        Optional<User> receiver = userRepository.findByUsername(messageDTO.getReceiverUsername());

        if (sender.isEmpty() || receiver.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid sender or receiver username.");
        }

        Message message = new Message(messageDTO.getContent(), sender.orElse(null), receiver.orElse(null));
        messageService.save(message);
        return ResponseEntity.ok("Message sent successfully.");
    }

    @GetMapping("/received/{username}")
    public ResponseEntity<?> getReceivedMessages(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }
        return ResponseEntity.ok(messageService.getReceivedMessages(username));
    }
}
