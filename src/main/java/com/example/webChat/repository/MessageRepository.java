package com.example.webChat.repository;

import com.example.webChat.model.Message;
import com.example.webChat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


    List<Message> findByReceiver(User receiver);
}
