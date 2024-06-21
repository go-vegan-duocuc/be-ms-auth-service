package cl.govegan.msauthservice.repository;

import cl.govegan.msauthservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername (String username);

    Optional<User> findByEmail (String email);

    Optional<User> findByResetToken (String username, String email);
}
