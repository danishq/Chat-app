package com.example.chat.controller;

import com.example.chat.model.User;
import com.example.chat.model.ChatMessage;
import com.example.chat.repository.UserRepository;
import com.example.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class PageController {

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private MessageRepository messageRepo;

  @GetMapping("/")
  public String home() {
    return "redirect:/login";
  }

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @PostMapping("/login")
  public String loginUser(@RequestParam String username,
      @RequestParam String password,
      HttpSession session,
      Model model) {
    try {
      // Add debug logging
      System.out.println("Login attempt for username: " + username);

      // Check if username is provided
      if (username == null || username.trim().isEmpty()) {
        model.addAttribute("error", "Username is required");
        return "login";
      }

      // Check if password is provided
      if (password == null || password.trim().isEmpty()) {
        model.addAttribute("error", "Password is required");
        return "login";
      }

      User user = userRepo.findByUsername(username.trim());
      System.out.println("User found: " + (user != null ? user.getUsername() : "null"));

      if (user == null) {
        model.addAttribute("error", "User not found. Please register first.");
        model.addAttribute("showRegister", true);
        return "login";
      }

      // Check password (consider using BCrypt for production)
      if (!user.getPassword().equals(password)) {
        model.addAttribute("error", "Invalid password");
        return "login";
      }

      // Successful login
      session.setAttribute("username", username.trim());
      session.setAttribute("loggedInUser", user);
      return "redirect:/chat";

    } catch (Exception e) {
      System.err.println("Login error: " + e.getMessage());
      e.printStackTrace();
      model.addAttribute("error", "Login failed: " + e.getMessage());
      model.addAttribute("showRegister", true);
      return "login";
    }
  }

  @GetMapping("/register")
  public String registerPage() {
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(@RequestParam String username,
      @RequestParam String password,
      Model model) {
    try {
      // Validate input
      if (username == null || username.trim().isEmpty()) {
        model.addAttribute("error", "Username is required");
        return "register";
      }

      if (password == null || password.trim().isEmpty()) {
        model.addAttribute("error", "Password is required");
        return "register";
      }

      // Check username length and format
      if (username.trim().length() < 3) {
        model.addAttribute("error", "Username must be at least 3 characters long");
        return "register";
      }

      if (password.length() < 4) {
        model.addAttribute("error", "Password must be at least 4 characters long");
        return "register";
      }

      // Check if username already exists
      if (userRepo.findByUsername(username.trim()) != null) {
        model.addAttribute("error", "Username already exists. Please choose a different one.");
        return "register";
      }

      // Create new user
      User user = new User();
      user.setUsername(username.trim());
      user.setPassword(password); // In production, you should hash this password

      User savedUser = userRepo.save(user);
      System.out.println("New user registered: " + savedUser.getUsername());

      model.addAttribute("success", "Registration successful! Please login with your credentials.");
      return "login";

    } catch (Exception e) {
      System.err.println("Registration error: " + e.getMessage());
      e.printStackTrace();
      model.addAttribute("error", "Registration failed: " + e.getMessage());
      return "register";
    }
  }

  @GetMapping("/chat")
  public String chatPage(HttpSession session, Model model) {
    String username = (String) session.getAttribute("username");
    if (username == null) {
      return "redirect:/login";
    }

    model.addAttribute("username", username);

    try {
      // Load messages relevant to the logged-in user (sent by or received by)
      // Order them newest to oldest for "latest at top" display
      List<ChatMessage> messages = messageRepo.findTopNMessagesForUser(username, 50); // <--- CHANGE THIS LINE
      model.addAttribute("messages", messages);
    } catch (Exception e) {
      System.err.println("Error loading messages: " + e.getMessage());
      model.addAttribute("error", "Could not load messages");
    }

    return "chat";
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/login";
  }
}
