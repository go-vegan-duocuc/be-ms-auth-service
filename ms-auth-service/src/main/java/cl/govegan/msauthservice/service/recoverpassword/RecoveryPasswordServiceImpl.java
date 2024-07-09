package cl.govegan.msauthservice.service.recoverpassword;

import java.util.Optional;
import java.util.Random;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import cl.govegan.msauthservice.model.RecoveryPasswordCode;
import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.repository.RecoveryPasswordCodeRepository;
import cl.govegan.msauthservice.service.email.EmailService;
import cl.govegan.msauthservice.service.jwt.JwtService;
import cl.govegan.msauthservice.service.userservice.UserService;
import lombok.RequiredArgsConstructor;
import cl.govegan.msauthservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RecoveryPasswordServiceImpl implements RecoveryPasswordService {

   private final Random random = new Random();
   private final RecoveryPasswordCodeRepository recoveryPasswordCodeRepository;
   private final EmailService emailService;
   private final UserRepository userRepository;

   @Override
   public RecoveryPasswordCode sendRecoveryCodeByEmail(String email) {

      Context context = new Context();

      Optional<User> userOptional = userRepository.findByEmail(email);

      if (userOptional.isPresent()) {
         deleteRecoverPasswordCode(userOptional.get().getUsername());
      } else {
         throw new RuntimeException("User not found");
      }

      RecoveryPasswordCode recoveryPasswordCode = RecoveryPasswordCode.builder()
            .code(generateRecoveryCode())
            .username(userOptional.get().getUsername())
            .used(false)
            .expirationTime(
                  System.currentTimeMillis() + 86400000)
            .expired(false)
            .build();

      recoveryPasswordCodeRepository.save(recoveryPasswordCode);

      context.setVariable("username", userOptional.get().getUsername());
      context.setVariable("recoveryCode", recoveryPasswordCode.getCode());

      try {
         emailService.sendEmail(email, context);
      } catch (Exception e) {
         e.printStackTrace();
      }

      return recoveryPasswordCode;

   }

   @Override
   public void deleteRecoverPasswordCode(String code) {
      recoveryPasswordCodeRepository.deleteByCode(code);
   }

   private String generateRecoveryCode() {
      StringBuilder recoveryCode = new StringBuilder();

      for (int i = 0; i < 10; i++) {
         int randomNumber = this.random.nextInt(10);
         recoveryCode.append(randomNumber);
      }

      return recoveryCode.toString();
   }

}