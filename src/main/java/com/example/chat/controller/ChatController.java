package com.example.chat.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.chat.model.ChatMessage; // <--- ADD THIS IMPORT
import com.example.chat.model.User;
import com.example.chat.repository.MessageRepository;
import com.example.chat.repository.UserRepository;

@Controller
public class ChatController {

  @Autowired
  private MessageRepository messageRepo;

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private SimpMessagingTemplate messagingTemplate; // <--- ADD THIS

  // REMOVE @SendTo("/topic/public")
  @MessageMapping("/chat.sendMessage")
  public void sendMessage(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
    Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
    if (sessionAttributes == null) {
      System.err.println("No session attributes available");
      return;
    }

    String senderUsername = (String) sessionAttributes.get("username");

    if (senderUsername == null) {
      System.err.println("No username in session attributes");
      return;
    }

    try {
      User sender = userRepo.findByUsername(senderUsername);
      if (sender == null) {
        messagingTemplate.convertAndSendToUser(
            senderUsername,
            "/queue/errors",
            createErrorMessage("System", senderUsername, "Sender user not found"));
        return;
      }

      User recipient = userRepo.findByUsername(message.getRecipientUsername());
      if (recipient == null) {
        messagingTemplate.convertAndSendToUser(
            senderUsername,
            "/queue/errors",
            createErrorMessage("System", senderUsername,
                "Recipient '" + message.getRecipientUsername() + "' not found"));
        return;
      }

      message.setSender(sender);
      message.setRecipient(recipient);
      message.setTimestamp(LocalDateTime.now());
      message.setSenderUsername(sender.getUsername()); // Set transient fields for frontend
      message.setRecipientUsername(recipient.getUsername()); // Set transient fields for frontend

      ChatMessage savedMessage = messageRepo.save(message);

      // Send message to recipient's private queue
      // Format: /user/{username}/queue/messages
      messagingTemplate.convertAndSendToUser(
          recipient.getUsername(),
          "/queue/messages",
          savedMessage);

      // Also send message to sender's private queue so they see their own sent
      // messages
      if (!sender.getUsername().equals(recipient.getUsername())) { // Avoid sending duplicate if self-chatting
        messagingTemplate.convertAndSendToUser(
            sender.getUsername(),
            "/queue/messages",
            savedMessage);
      }

    } catch (Exception e) {
      System.err.println("Error sending message: " + e.getMessage());
      e.printStackTrace();
      messagingTemplate.convertAndSendToUser(
          senderUsername,
          "/queue/errors",
          createErrorMessage("System", senderUsername, "Error: " + e.getMessage()));
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
