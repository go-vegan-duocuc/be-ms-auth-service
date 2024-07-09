package cl.govegan.msauthservice.service.refreshtoken;

import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.govegan.msauthservice.model.RefreshToken;

@Service
public interface RefreshTokenService {

      public String getUsernameFromRefreshToken(String token);
   
      public RefreshToken createRefreshToken(String username);
   
      public void deleteRefreshTokenByUser(String username);
   
      public Optional<RefreshToken> verifyExpiration(RefreshToken token);
   
      public Optional<RefreshToken> findByToken(String token);
   
      public Optional<RefreshToken> findByUsername(String username);
}
