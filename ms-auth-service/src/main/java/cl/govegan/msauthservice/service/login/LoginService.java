package cl.govegan.msauthservice.service.login;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import cl.govegan.msauthservice.model.RefreshToken;
import cl.govegan.msauthservice.service.jwt.JwtService;
import cl.govegan.msauthservice.service.refreshtoken.RefreshTokenService;
import cl.govegan.msauthservice.service.userservice.CustomUserDetailsService;
import cl.govegan.msauthservice.web.request.LoginRequest;
import cl.govegan.msauthservice.web.response.TokenPayload;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    public TokenPayload login (LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return TokenPayload.builder()
                    .token(jwtService.generateToken(userDetails))
                    .refreshToken(refreshTokenService.createRefreshToken(userDetails.getUsername()).getToken())
                    .build();
        } catch (AuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
    }

    public TokenPayload refresh (String token) {

        RefreshToken refreshToken = refreshTokenService.findByToken(token).orElseThrow();
        if (refreshToken.getExpiration().compareTo(Instant.now()) < 0) {
            refreshTokenService.deleteRefreshTokenByUser(refreshToken.getUsername());
            throw new AuthenticationServiceException("Refresh token expired");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(refreshToken.getUsername());

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return TokenPayload.builder()
                .token(jwtService.generateToken(userDetails))
                .refreshToken(newRefreshToken.getToken())
                .build();
    }
}
