package cl.govegan.msauthservice.service.userservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.repository.UserRepository;
import cl.govegan.msauthservice.service.jwt.JwtService;
import cl.govegan.msauthservice.web.request.NewEmailRequest;
import cl.govegan.msauthservice.web.request.NewPasswordRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

   private final UserRepository userRepository;
   private final JwtService jwtService;
   private final PasswordEncoder passwordEncoder;
   private final RestTemplate restTemplate;

   @Value("${profile.service.url}")
   private String profileServiceUrl;

   @Value("${profile.service.prefix}")
   private String profileServicePrefix;

   @Value("${profile.service.mapping.profile}")
   private String profileServiceMappingProfile;

   public User getUserDetails(Authentication authentication) {

      String jwt = authentication.getPrincipal().toString();
      String username = jwtService.extractUsername(jwt);

      return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

   }

   public void updatePassword(NewPasswordRequest newPasswordRequest, Authentication authentication) {

      String jwt = authentication.getPrincipal().toString();
      String username = jwtService.extractUsername(jwt);

      User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

      if (!passwordEncoder.matches(newPasswordRequest.getOldPassword(), user.getPassword())) {
         throw new RuntimeException("Old password is incorrect");
      } else if (passwordEncoder.matches(newPasswordRequest.getNewPassword(), user.getPassword())) {
         throw new RuntimeException("New password is the same as the old password");
      } else {
         user.setPassword(passwordEncoder.encode(newPasswordRequest.getNewPassword()));
         userRepository.save(user);
      }
   }

   public String updateEmail(NewEmailRequest newEmailRequest, Authentication authentication) {

      String jwt = authentication.getPrincipal().toString();
      String username = jwtService.extractUsername(jwt);

      User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

      if (userRepository.findByEmail(newEmailRequest.getNewEmail()).isPresent()) {
         throw new RuntimeException("Email already exists");
      } else if (newEmailRequest.getNewEmail().equals(user.getEmail())) {
         throw new RuntimeException("New email is the same as the old email");
      }

      user.setEmail(newEmailRequest.getNewEmail());
      userRepository.save(user);

      return user.getEmail();
   }

   public void deleteAccount(Authentication authentication) {

      String jwt = authentication.getPrincipal().toString();
      String username = jwtService.extractUsername(jwt);

      User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

      String deleteProfileUrl = profileServiceUrl + profileServicePrefix + profileServiceMappingProfile
            + "/delete-user-profile";

      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + jwt);
      HttpEntity<String> request = new HttpEntity<>(null, headers);

      ResponseEntity<String> response = restTemplate.exchange(deleteProfileUrl, HttpMethod.DELETE, request,
            String.class);

      if (response.getStatusCode().value() != HttpServletResponse.SC_OK) {
         throw new RuntimeException("Error deleting profile");
      }

      userRepository.delete(user);
   }

   @Override
   public User getUserByEmail(String email) {
      return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
   }

}
