package cl.govegan.msauthservice.service.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.aggregation.VariableOperators.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import cl.govegan.msauthservice.exception.DatabaseErrorException;
import cl.govegan.msauthservice.exception.EmailAlreadyExistsException;
import cl.govegan.msauthservice.exception.UsernameAlreadyExistsException;
import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.repository.UserRepository;
import cl.govegan.msauthservice.service.email.EmailService;
import cl.govegan.msauthservice.service.jwt.JwtService;
import cl.govegan.msauthservice.web.request.RegisterRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class RegisterServiceImpl implements RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    @Override
    public User register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = null;
        try {
            user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

            user = userRepository.save(user);  // This will populate the id

            String jwt = jwtService.generateToken(user);

            createDefaultUserProfile(jwt, user.getId());

            sendWelcomeEmail(user);

            return user;
        } catch (Exception e) {
            logger.error("Error during user registration process", e);
            if (user != null && user.getId() != null) {
                userRepository.deleteById(user.getId());
            }
            throw new DatabaseErrorException("Error during registration process: " + e.getMessage());
        }
    }

    private void createDefaultUserProfile(String jwt, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity<String> request = new HttpEntity<>(userId, headers);

        String defaultProfileServiceUrl = "http://localhost:8082/api/v1/user-profile/default-profile";

        ResponseEntity<Object> response = restTemplate.exchange(defaultProfileServiceUrl, HttpMethod.POST, request, Object.class);

        if (response.getStatusCode().value() != HttpServletResponse.SC_OK) {
            throw new RuntimeException("Error creating profile: " + response.getStatusCode());
        }
    }

    private void sendWelcomeEmail(User user) throws MessagingException {
        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
    }
}