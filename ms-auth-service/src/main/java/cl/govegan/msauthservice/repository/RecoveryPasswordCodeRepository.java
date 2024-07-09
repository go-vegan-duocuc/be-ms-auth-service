package cl.govegan.msauthservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import cl.govegan.msauthservice.model.RecoveryPasswordCode;

public interface RecoveryPasswordCodeRepository extends MongoRepository<RecoveryPasswordCode, String> {

   Optional<RecoveryPasswordCode> findByCode(String code);

   Optional<RecoveryPasswordCode> findByUsername(String username);

   void deleteByCode(String code);

}
