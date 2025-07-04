package com.example.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class }) // Add this exclude
public class ChatAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChatAppApplication.class, args);
  }
}
