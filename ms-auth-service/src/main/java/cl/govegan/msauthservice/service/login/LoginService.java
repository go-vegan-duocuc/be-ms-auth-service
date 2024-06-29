package cl.govegan.msauthservice.service.login;

import cl.govegan.msauthservice.service.jwt.JwtService;
import cl.govegan.msauthservice.service.userservice.CustomUserDetailsService;
import cl.govegan.msauthservice.web.request.LoginRequest;
import cl.govegan.msauthservice.web.response.TokenLoad;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public TokenLoad login (LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return TokenLoad.builder()
                    .token(jwtService.generateToken(userDetails))
                    .refreshToken(jwtService.generateRefreshToken(userDetails))
                    .build();
        } catch (AuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
    }

    public TokenLoad refreshToken (String refreshToken) {
        String username = jwtService.getUsernameFromToken(refreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (Boolean.TRUE.equals(jwtService.validateToken(refreshToken, userDetails))) {
            return TokenLoad.builder()
                    .token(jwtService.generateToken(userDetails))
                    .refreshToken(jwtService.generateRefreshToken(userDetails))
                    .build();
        } else {
            throw new AuthenticationServiceException("Invalid refresh token");
        }
    }
}
