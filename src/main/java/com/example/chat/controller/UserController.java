package com.example.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserRepository userRepo;

  // TODO: Add user-related API endpoints here if needed
  // For now, this controller is empty but ready for future user management features
}