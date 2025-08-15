package com.pickfolio.contest.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickfolio.contest.domain.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.error("Authentication failed: {}", authException.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "Authentication failed. Please provide a valid token: " + authException.getMessage()
        );

        // Write the custom error response as JSON to the response body
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}