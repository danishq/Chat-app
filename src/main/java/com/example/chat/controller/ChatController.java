package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import com.example.chat.model.User;
import com.example.chat.repository.MessageRepository;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
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
  public ChatMessage sendMessage(@Payload ChatMessage message) {
    try {
      // Use the sender username from the message (sent from frontend)
      User sender = userRepo.findByUsername(message.getSenderUsername());
      if (sender == null) {
        // Return error message
        ChatMessage errorMessage = new ChatMessage();
        errorMessage.setSenderUsername("System");
        errorMessage.setRecipientUsername(message.getSenderUsername());
        errorMessage.setContent("Error: Sender '" + message.getSenderUsername() + "' not found. Please log in again.");
        errorMessage.setTimestamp(LocalDateTime.now());
        return errorMessage;
      }

      // Find recipient
      User recipient = userRepo.findByUsername(message.getRecipientUsername());
      if (recipient == null) {
        // Return error message
        ChatMessage errorMessage = new ChatMessage();
        errorMessage.setSenderUsername("System");
        errorMessage.setRecipientUsername(message.getSenderUsername());
        errorMessage.setContent("Error: Recipient '" + message.getRecipientUsername()
            + "' not found. Please make sure they are registered.");
        errorMessage.setTimestamp(LocalDateTime.now());
        return errorMessage;
      }

      // Set up the message
      message.setSender(sender);
      message.setRecipient(recipient);
      message.setTimestamp(LocalDateTime.now());

      // Save to database
      ChatMessage savedMessage = messageRepo.save(message);

      // Set the usernames for the WebSocket response
      savedMessage.setSenderUsername(sender.getUsername());
      savedMessage.setRecipientUsername(recipient.getUsername());

      return savedMessage;

    } catch (Exception e) {
      // Return error message
      ChatMessage errorMessage = new ChatMessage();
      errorMessage.setSenderUsername("System");
      errorMessage.setRecipientUsername(message.getSenderUsername());
      errorMessage.setContent("Error sending message: " + e.getMessage());
      errorMessage.setTimestamp(LocalDateTime.now());
      return errorMessage;
    }
  }
}
