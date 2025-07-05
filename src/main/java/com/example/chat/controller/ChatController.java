package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import com.example.chat.model.User;
import com.example.chat.repository.MessageRepository;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

  @Autowired
  private MessageRepository messageRepo;

  @Autowired
  private UserRepository userRepo;

  @MessageMapping("/chat.sendMessage")
  @SendTo("/topic/public")
  public ChatMessage sendMessage(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
    try {
      String username = (String) headerAccessor.getSessionAttributes().get("username");
      if (username == null) {
        return createErrorMessage("System", "unknown", "Not authenticated");
      }

      User sender = userRepo.findByUsername(username);
      if (sender == null) {
        return createErrorMessage("System", username, "User not found");
      }

      User recipient = userRepo.findByUsername(message.getRecipientUsername());
      if (recipient == null) {
        return createErrorMessage("System", username,
            "Recipient '" + message.getRecipientUsername() + "' not found");
      }

      message.setSender(sender);
      message.setRecipient(recipient);
      message.setTimestamp(LocalDateTime.now());
      message.setSenderUsername(sender.getUsername());
      message.setRecipientUsername(recipient.getUsername());

      return messageRepo.save(message);
    } catch (Exception e) {
      return createErrorMessage("System", "unknown", "Error: " + e.getMessage());
    }
  }

  private ChatMessage createErrorMessage(String sender, String recipient, String content) {
    ChatMessage error = new ChatMessage();
    error.setSenderUsername(sender);
    error.setRecipientUsername(recipient);
    error.setContent(content);
    error.setTimestamp(LocalDateTime.now());
    return error;
  }
}
