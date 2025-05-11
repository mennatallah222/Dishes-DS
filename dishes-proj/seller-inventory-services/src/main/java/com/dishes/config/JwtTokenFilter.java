package com.dishes.config;  // Or your preferred package

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dishes.jwt.JwtTokenUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtTokenFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }

    try {
        final String token = authHeader.substring(7);
        
        if (!jwtTokenUtil.validateToken(token)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT Token");
            return;
        }

        Long sellerId = jwtTokenUtil.extractSellerId(token);
        
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                sellerId, null, Collections.emptyList());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        
    }
    catch (Exception e) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Token");
        return;
    }

    filterChain.doFilter(request, response);
}

}