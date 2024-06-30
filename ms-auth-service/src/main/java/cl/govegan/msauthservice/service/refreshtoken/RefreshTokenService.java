package cl.govegan.msauthservice.service.refreshtoken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cl.govegan.msauthservice.exception.DatabaseErrorException;
import cl.govegan.msauthservice.model.RefreshToken;
import cl.govegan.msauthservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
   
   @Value("${jwt.refresh.expiration}")
   private Long refreshTokenExpirationInMs;

   private final RefreshTokenRepository refreshTokenRepository;

   public String getUsernameFromRefreshToken(String token) {
      Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
      return refreshToken.map(RefreshToken::getUsername).orElse(null);
   }

   public RefreshToken createRefreshToken(String username) {
      RefreshToken refreshToken = RefreshToken.builder()
            .username(username)
            .token(UUID.randomUUID().toString())
            .expiration(Instant.now().plusMillis(refreshTokenExpirationInMs))
            .build();

            try {
               return refreshTokenRepository.save(refreshToken);
            } catch (DatabaseErrorException e) {
               throw new DatabaseErrorException("Error creating refresh token");
            }
   }

   public void deleteRefreshTokenByUser (String username) {
      refreshTokenRepository.deleteByUsername(username);
   }

   public Optional<RefreshToken> verifyExpiration(RefreshToken token) {
      if (token.getExpiration().compareTo(Instant.now()) < 0) {
         refreshTokenRepository.delete(token);
         return Optional.empty();
      }
      return Optional.of(token);
   }

   public Optional<RefreshToken> findByToken(String token) {
      return refreshTokenRepository.findByToken(token);
   }
}
