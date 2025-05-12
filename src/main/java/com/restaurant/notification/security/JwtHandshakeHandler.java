package com.restaurant.notification.security;


import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class JwtHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtTokenValidator tokenValidator;

    public JwtHandshakeHandler(JwtTokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        // Get token from query parameter or header
        String token = request.getHeaders().getFirst("Authorization");

        if (token == null) {
            token = request.getURI().getQuery();
        }

        if (token != null && token.startsWith("Bearer=")) {
            token = token.substring(7);
        }

        if (token != null) {
            try {
                String userId = tokenValidator.validateAndExtractUser(token);
                return () -> userId;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
