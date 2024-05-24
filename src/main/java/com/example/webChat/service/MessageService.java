package com.example.webChat.service;

import com.example.webChat.dto.MessageDTO;
import com.example.webChat.model.Message;
import com.example.webChat.model.User;
import com.example.webChat.repository.MessageRepository;
import com.example.webChat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    // This method is to get received messages for a specific user
    public List<MessageDTO> getReceivedMessages(String username) {
        User receiver = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Message> messages = messageRepository.findByReceiver(receiver);

        return messages.stream()
                .map(message -> new MessageDTO(message.getContent(), message.getSender().getUsername(), message.getReceiver().getUsername()))
                .collect(Collectors.toList());
    }
}
