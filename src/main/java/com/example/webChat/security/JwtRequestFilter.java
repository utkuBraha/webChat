package com.example.webChat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {


        if (shouldNotFilter(request.getRequestURI(), response)) {
            chain.doFilter(request, response);
        } else {
            final String authorizationHeader = request.getHeader("Authorization");

            String userKeyId = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                userKeyId = jwtUtil.extractUserId(jwt);
            }

            if (jwt != null && jwtUtil.validateToken(jwt)) {
                if (userKeyId != null ) {
                    // Add the details to the request attributes
                    request.setAttribute("userKeyId", userKeyId);

                    // Continue with the chain
                    chain.doFilter(request, response);
                } else {
                    // The token is valid, but does not contain userId and/or deviceId
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "The JWT token is missing required claims.");
                }
            } else {
                // The token is invalid or not present
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "The JWT token is invalid or not present.");
            }
        }
    }

    private boolean shouldNotFilter(String path, HttpServletResponse response) {
        List<String> excludeUrlPatterns = Arrays.asList("/v1/login","/v1/login/*", "/v1/register", "/v1/register/*", "/v1/index", "/v1/index/*",
                "/register","/register/*",


                "/auth/generateUsername", "/swagger-ui.html", "/swagger-ui/.*", "/v3/api-docs/.*", "/swagger-resources/.*", "/webjars/.*"
                , "/v3/api-docs", "auth/devices/.*","/auth/devices", "/auth/register","/auth/login/.*","/successPage.*","/errorPage.*"
                ,"/auth/login.*","/login.*","/login/.*", "/login","/auth/spin", "/auth/spin/.*",
                "/auth/loginPage","/auth/loginPage/*","/auth/generateToken","/auth/join","/auth/join/.*");
        boolean shouldNotFilter = excludeUrlPatterns.stream().anyMatch(path::matches);
        return shouldNotFilter;
    }

}

