package com.example.webChat.service;

import com.example.webChat.model.Message;
import com.example.webChat.model.User;
import com.example.webChat.repository.MessageRepository;
import com.example.webChat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }




    // This method is to save a message into the database
    public void save(Message message) {
        System.out.println("Message saved successfully");
        messageRepository.save(message);
    }

    public List<Message> getReceivedMessages(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }
        return messageRepository.findAllByReceiver(user.orElse(null));
    }
}
