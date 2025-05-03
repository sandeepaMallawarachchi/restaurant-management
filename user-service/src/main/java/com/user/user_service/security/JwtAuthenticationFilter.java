package com.user.user_service.security;

import com.user.user_service.services.CustomUserDetailsService;
import com.user.user_service.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Get JWT token from the Authorization header
            String jwt = getJwtFromRequest(request);


            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                // Get username from JWT
                String username = jwtUtil.getUsernameFromJWT(jwt);


                // Extract userId from JWT
                Long userId = jwtUtil.getUserIdFromJWT(jwt);


                // Get roles from JWT
                Set<String> roles = jwtUtil.getRolesFromJWT(jwt);
                System.out.println(roles);
                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Create authentication token
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Set authentication details
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Add userId to request attributes
                request.setAttribute("userId", userId);
                request.setAttribute("roles", roles);
            }
        } catch (Exception ex) {
            // Log the exception
            System.err.println("Could not set user authentication in security context: " + ex);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    // Helper method to extract JWT token from the Authorization header
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // The token is expected to be in the format "Bearer <token>"
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

}
