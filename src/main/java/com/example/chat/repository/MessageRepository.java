package com.example.chat.repository;

import com.example.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

  List<ChatMessage> findTop50ByOrderByTimestampDesc();

  // Get messages between two users
  @Query("SELECT m FROM ChatMessage m WHERE " +
      "(m.sender.username = ?1 AND m.recipient.username = ?2) OR " +
      "(m.sender.username = ?2 AND m.recipient.username = ?1) " +
      "ORDER BY m.timestamp ASC")
  List<ChatMessage> findMessagesBetweenUsers(String user1, String user2);

  // NEW: Get messages sent by or received by a specific user
  @Query("SELECT m FROM ChatMessage m WHERE " +
      "m.sender.username = :username OR m.recipient.username = :username " +
      "ORDER BY m.timestamp DESC") // Get newest first
  List<ChatMessage> findMessagesForUser(String username);

  // NEW: Get the top N messages for a specific user, newest first
  @Query(value = "SELECT * FROM messages m WHERE m.user_id = (SELECT id FROM users WHERE username = :username) OR m.recipient_id = (SELECT id FROM users WHERE username = :username) ORDER BY m.timestamp DESC LIMIT :limit", nativeQuery = true)
  List<ChatMessage> findTopNMessagesForUser(String username, int limit);
}
