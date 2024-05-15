//package com.example.webChat.controller;
//
//import com.example.webChat.dto.MessageDTO;
//import com.example.webChat.model.Message;
//import com.example.webChat.model.User;
//import com.example.webChat.repository.UserRepository;
//import com.example.webChat.service.MessageService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/messages")
//public class MessageController {
//
//    private final MessageService messageService;
//    private final UserRepository userRepository;
//
//    public MessageController(MessageService messageService, UserRepository userRepository) {
//        this.messageService = messageService;
//        this.userRepository = userRepository;
//    }
//
//    @PostMapping("/send")
//    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO messageDTO) {
//        Optional<User> sender = userRepository.findByUsername(messageDTO.getSenderUsername());
//        Optional<User> receiver = userRepository.findByUsername(messageDTO.getReceiverUsername());
//
//        if (sender.isEmpty()) {
//            return ResponseEntity.badRequest().body("Sender username not found: " + messageDTO.getSenderUsername());
//        }
//        if (receiver.isEmpty()) {
//            return ResponseEntity.badRequest().body("Receiver username not found: " + messageDTO.getReceiverUsername());
//        }
//
//        Message message = new Message(messageDTO.getContent(), sender.get(), receiver.get());
//        messageService.save(message);
//        return ResponseEntity.ok("Message sent successfully.");
//    }
//
//
//    @GetMapping("/received/{username}")
//    public ResponseEntity<?> getReceivedMessages(@PathVariable String username) {
//        Optional<User> user = userRepository.findByUsername(username);
//        if (user.isEmpty()) {
//            return ResponseEntity.badRequest().body("User not found.");
//        }
//        return ResponseEntity.ok(messageService.getReceivedMessages(username));
//    }
//}
package com.example.webChat.controller;

import com.example.webChat.dto.MessageDTO;
import com.example.webChat.model.Message;
import com.example.webChat.model.User;
import com.example.webChat.repository.UserRepository;
import com.example.webChat.service.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO messageDTO, HttpSession session) {
        String senderUsername = (String) session.getAttribute("username");

        if (senderUsername == null) {
            return ResponseEntity.status(401).body("You are not logged in.");
        }

        Optional<User> sender = userRepository.findByUsername(senderUsername);
        Optional<User> receiver = userRepository.findByUsername(messageDTO.getReceiverUsername());

        if (receiver.isEmpty()) {
            return ResponseEntity.badRequest().body("Receiver username not found: " + messageDTO.getReceiverUsername());
        }

        Message message = new Message(messageDTO.getContent(), sender.get(), receiver.get());
        messageService.save(message);
        return ResponseEntity.ok("Message sent successfully.");
    }

    @GetMapping("/received")
    public ResponseEntity<?> getReceivedMessages(HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return ResponseEntity.status(401).body("You are not logged in.");
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        List<MessageDTO> receivedMessages = messageService.getReceivedMessages(username);
        return ResponseEntity.ok(receivedMessages);
    }

}
