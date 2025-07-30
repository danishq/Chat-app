package com.example.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // config.enableSimpleBroker("/topic"); // <--- REMOVE OR COMMENT OUT THIS LINE
    config.enableSimpleBroker("/user"); // <--- ADD THIS FOR USER-SPECIFIC QUEUES
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .withSockJS()
        .setInterceptors(new HttpSessionHandshakeInterceptor());
    // Your custom interceptor HttpHandshakeInterceptor is also useful if you need
    // to pass more custom attributes.
    // For now, HttpSessionHandshakeInterceptor is good enough as it passes all
    // session attributes.
  }
}
