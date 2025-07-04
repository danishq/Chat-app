package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import com.example.chat.model.User;
import com.example.chat.repository.MessageRepository;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

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
      User user = userRepo.findByUsername(username);
      if (user == null || !user.getPassword().equals(password)) {
        model.addAttribute("error", "Invalid username or password");
        return "login";
      }

      // Store user in session
      session.setAttribute("loggedInUser", user);
      return "redirect:/chat";
    } catch (Exception e) {
      e.printStackTrace();
      model.addAttribute("error", "Login failed: " + e.getMessage());
      return "login";
    }
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.removeAttribute("loggedInUser");
    return "redirect:/login";
  }

  @GetMapping("/chat")
  public String chatPage(Model model, HttpSession session) {
    User loggedInUser = (User) session.getAttribute("loggedInUser");
    if (loggedInUser == null) {
      return "redirect:/login";
    }

    model.addAttribute("username", loggedInUser.getUsername());
    model.addAttribute("messages", messageRepo.findAll());
    return "chat";
  }

  @GetMapping("/register")
  public String registerPage() {
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(@RequestParam String username,
      @RequestParam String password,
      HttpSession session,
      Model model) {
    try {
      if (userRepo.findByUsername(username) != null) {
        model.addAttribute("error", "Username already exists");
        return "register";
      }

      User newUser = new User();
      newUser.setUsername(username);
      newUser.setPassword(password); // NOTE: Should hash later
      newUser = userRepo.save(newUser);

      // Auto-login after registration
      session.setAttribute("loggedInUser", newUser);
      return "redirect:/chat";
    } catch (Exception e) {
      e.printStackTrace();
      model.addAttribute("error", "Something went wrong: " + e.getMessage());
      return "register";
    }
  }

  @PostMapping("/send-message")
  public String sendMessage(@RequestParam String recipient,
      @RequestParam String content,
      HttpSession session,
      Model model) {
    try {
      User loggedInUser = (User) session.getAttribute("loggedInUser");
      if (loggedInUser == null) {
        return "redirect:/login";
      }

      User recipientUser = userRepo.findByUsername(recipient);
      if (recipientUser == null) {
        model.addAttribute("error", "Recipient not found");
        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("messages", messageRepo.findAll());
        return "chat";
      }

      ChatMessage message = new ChatMessage();
      message.setSender(loggedInUser);
      message.setRecipient(recipientUser);
      message.setContent(content);
      message.setTimestamp(LocalDateTime.now());
      messageRepo.save(message);

      return "redirect:/chat";
    } catch (Exception e) {
      e.printStackTrace();
      model.addAttribute("error", "Failed to send message: " + e.getMessage());
      User loggedInUser = (User) session.getAttribute("loggedInUser");
      model.addAttribute("username", loggedInUser != null ? loggedInUser.getUsername() : "Guest");
      model.addAttribute("messages", messageRepo.findAll());
      return "chat";
    }
  }
}
