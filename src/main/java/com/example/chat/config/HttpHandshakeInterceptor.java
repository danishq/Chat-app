package com.example.chat.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

public class HttpHandshakeInterceptor implements HandshakeInterceptor {

  @Override
  public boolean beforeHandshake(ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes) throws Exception {

    // Transfer HTTP session attributes to WebSocket session
    if (request instanceof org.springframework.http.server.ServletServerHttpRequest) {
      org.springframework.http.server.ServletServerHttpRequest servletRequest = (org.springframework.http.server.ServletServerHttpRequest) request;

      HttpSession session = servletRequest.getServletRequest().getSession(false);
      if (session != null) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
          attributes.put("username", username);
        }
      }
    }
    return true;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Exception exception) {
    // Do nothing after handshake
  }
}
