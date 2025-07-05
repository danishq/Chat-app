package com.example.chat.repository;

import com.example.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

  // Get recent messages ordered by timestamp (newest first)
  List<ChatMessage> findTop50ByOrderByTimestampDesc();

  // Get messages between two users
  @Query("SELECT m FROM ChatMessage m WHERE " +
      "(m.sender.username = ?1 AND m.recipient.username = ?2) OR " +
      "(m.sender.username = ?2 AND m.recipient.username = ?1) " +
      "ORDER BY m.timestamp ASC")
  List<ChatMessage> findMessagesBetweenUsers(String user1, String user2);
}
