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

    public Message sendMessage(String senderUsername, String receiverUsername, String content) {
        Optional<User> senderOpt = userRepository.findByUsername(senderUsername);
        Optional<User> receiverOpt = userRepository.findByUsername(receiverUsername);

        if (!senderOpt.isPresent() || !receiverOpt.isPresent()) {
            throw new IllegalArgumentException("Sender or receiver username not found.");
        }

        User sender = senderOpt.get();
        User receiver = receiverOpt.get();

        Message message = new Message(content, sender, receiver);
        return save(message);
    }


    // This method is to save a message into the database
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getReceivedMessages(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }
        return messageRepository.findAllByReceiver(user.orElse(null));
    }
}
