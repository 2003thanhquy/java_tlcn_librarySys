package com.spkt.librasys.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Định cấu hình Message Broker
        config.enableSimpleBroker("/topic","/queue"); // Đường dẫn cho các thông điệp gửi đến client
        config.setApplicationDestinationPrefixes("/app"); // Đường dẫn cho các thông điệp từ client đến server
        config.setUserDestinationPrefix("/user"); // Định nghĩa prefix cho các tin nhắn gửi đến người dùng cụ thể
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Khai báo endpoint WebSocket và cho phép CORS
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000");
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS(); // Thay bằng domain của bạn
    }

    @Autowired
    private JwtDecoder jwtDecoder;

    // Các phương thức khác...

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                      String authToken = accessor.getFirstNativeHeader("Authorization");
                    // Lấy access_token từ URL query parameter
                    //String authToken = accessor.getFirstNativeHeader("access_token");
                    System.out.println("authToken : " + authToken);
                    if (authToken != null) {
                        try {
                            // Giải mã và xác thực token
                            Jwt jwt = jwtDecoder.decode(authToken);
                            String userId = jwt.getSubject(); // Giả sử `userId` nằm trong `subject` của JWT

                            // Thiết lập đối tượng `Authentication` với `userId`
                            Authentication authentication = new JwtAuthenticationToken(jwt);
                            accessor.setUser(authentication); // Đặt `Principal` thành `userId`
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            System.out.println("User connected with userId: " + userId);
                        } catch (JwtException e) {
                            System.out.println("Token không hợp lệ: " + e.getMessage());
                        }
                    }
                }
                return message;
            }
        });
    }

}
