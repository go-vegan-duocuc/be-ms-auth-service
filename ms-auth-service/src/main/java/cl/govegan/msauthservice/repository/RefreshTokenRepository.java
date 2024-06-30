package cl.govegan.msauthservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import cl.govegan.msauthservice.model.RefreshToken;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String>{

   Optional<RefreshToken> findByToken(String token);
   void deleteByUsername(String username);

}
