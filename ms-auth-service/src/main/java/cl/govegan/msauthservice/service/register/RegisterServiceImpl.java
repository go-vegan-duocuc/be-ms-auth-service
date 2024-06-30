package cl.govegan.msauthservice.service.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.govegan.msauthservice.exception.DatabaseErrorException;
import cl.govegan.msauthservice.exception.EmailAlreadyExistsException;
import cl.govegan.msauthservice.exception.UsernameAlreadyExistsException;
import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.repository.UserRepository;
import cl.govegan.msauthservice.web.request.RegisterRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RegisterServiceImpl implements RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register (RegisterRequest registerRequest) {

        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        try {
            logger.debug("Registering user: {}", registerRequest);

            return userRepository.save(User.builder()
                    .username(registerRequest.getUsername())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .build());
        } catch (Exception e) {
            throw new DatabaseErrorException("Error saving user");
        }
    }
}
