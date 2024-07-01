package cl.govegan.msauthservice.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cl.govegan.msauthservice.exception.TokenValidationException;
import cl.govegan.msauthservice.service.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logService = LoggerFactory.getLogger(JwtRequestFilter.class);
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            final String jwtToken = authorizationHeader.substring(7);

            try {
                if (Boolean.TRUE.equals(jwtService.validateToken(jwtToken))) {
                    setAuthenticationContext(jwtToken, request);
                } else {
                    logService.warn("Invalid JWT token: {}", jwtToken);
                }
            } catch (TokenValidationException e) {
                logService.error("Token validation exception: {}", e.getMessage());
            } catch (Exception e) {
                logService.error("Error processing JWT token: {}", jwtToken, e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationContext(String jwtToken, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            jwtToken, null, null
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String username = jwtService.extractSubject(jwtToken);
        logService.info("Token is valid for user: {}", username);
    }
}