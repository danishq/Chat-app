package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import com.example.chat.model.User;
import com.example.chat.repository.MessageRepository;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class MessageController {

  @Autowired
  private MessageRepository messageRepo;

  @Autowired
  private UserRepository userRepo;

  @PostMapping("/message")
  public ChatMessage sendMessage(@RequestBody MessageRequest req) {
    User sender = userRepo.findById(req.getSenderId()).orElse(null);
    User recipient = userRepo.findByUsername(req.getRecipientUsername());

    if (sender == null || recipient == null)
      return null;

    ChatMessage msg = new ChatMessage();
    msg.setSender(sender);
    msg.setRecipient(recipient);
    msg.setContent(req.getContent());
    msg.setTimestamp(LocalDateTime.now());

    return messageRepo.save(msg);
  }

  static class MessageRequest {
    private Long senderId;
    private String recipientUsername;
    private String content;

    // Getters and Setters
    public Long getSenderId() {
      return senderId;
    }

    public void setSenderId(Long senderId) {
      this.senderId = senderId;
    }

    public String getRecipientUsername() {
      return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
      this.recipientUsername = recipientUsername;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }
}
