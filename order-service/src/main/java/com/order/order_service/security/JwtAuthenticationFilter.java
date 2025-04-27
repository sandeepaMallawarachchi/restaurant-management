package com.order.order_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            logger.info("Starting JWT authentication filter...");

            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                logger.debug("JWT found in request: {}", jwt);

                if (validateToken(jwt)) {
                    logger.info("JWT is valid. Proceeding to extract claims...");

                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                            .build()
                            .parseClaimsJws(jwt)
                            .getBody();

                    String username = claims.getSubject();
                    Long userId = claims.get("userId", Long.class);
                    List<String> roles = claims.get("role", List.class);

                    logger.debug("Extracted username: {}", username);
                    logger.debug("Extracted userId: {}", userId);
                    logger.debug("Extracted roles: {}", roles);

                    // Set userId as request attribute
                    request.setAttribute("userId", userId);
                    logger.info("Set userId as request attribute: {}", userId);

                    Collection<GrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.info("User authentication set in security context for userId: {}", userId);
                } else {
                    logger.warn("JWT validation failed. Token might be invalid or expired.");
                }
            } else {
                logger.warn("No JWT token found in request header.");
            }
        } catch (Exception ex) {
            logger.error("Exception occurred during setting user authentication", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            logger.debug("Extracted JWT from Authorization header.");
            return token;
        }
        logger.warn("Authorization header is missing or does not start with Bearer.");
        return null;
    }

    private boolean validateToken(String jwt) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(jwt);
            logger.debug("JWT successfully validated.");
            return true;
        } catch (Exception ex) {
            logger.error("Invalid JWT token", ex);
            return false;
        }
    }
}
