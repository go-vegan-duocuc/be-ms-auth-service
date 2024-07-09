package cl.govegan.msauthservice.service.recoverpassword;

import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import cl.govegan.msauthservice.model.RecoveryPasswordCode;
import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.repository.RecoveryPasswordCodeRepository;
import cl.govegan.msauthservice.service.email.EmailService;
import cl.govegan.msauthservice.service.jwt.JwtService;
import cl.govegan.msauthservice.service.userservice.UserService;
import cl.govegan.msauthservice.web.request.CheckCodeRequest;
import cl.govegan.msauthservice.web.request.ResetPasswordByCodeRequest;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import cl.govegan.msauthservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RecoveryPasswordServiceImpl implements RecoveryPasswordService {

   private final Random random = new Random();
   private final RecoveryPasswordCodeRepository recoveryPasswordCodeRepository;
   private final EmailService emailService;
   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;

   @Override
   public RecoveryPasswordCode sendRecoveryCodeByEmail(String email) throws MessagingException {
      RecoveryPasswordCode recoveryPasswordCode = saveRecoveryCode(email);
      Context context = createEmailContext(recoveryPasswordCode);
      emailService.sendEmail(email, context);
      return recoveryPasswordCode;
   }

   private Context createEmailContext(RecoveryPasswordCode recoveryPasswordCode) {
      Context context = new Context();
      context.setVariable("username", recoveryPasswordCode.getUsername());
      context.setVariable("recoveryCode", recoveryPasswordCode.getCode());
      return context;
   }

   private RecoveryPasswordCode saveRecoveryCode(String email) {
      return userRepository.findByEmail(email)
            .map(user -> {
               if (isExistingCodeActive(user.getUsername())) {
                  throw new RuntimeException("There is already an active recovery code for this user");
               }

               return recoveryPasswordCodeRepository.save(RecoveryPasswordCode.builder()
                     .code(generateRecoveryCode())
                     .username(user.getUsername())
                     .expirationTime(System.currentTimeMillis() + 86400000) // 24 hours
                     .build());
            })
            .orElseThrow(() -> new RuntimeException("User not found"));
   }

   private boolean isExistingCodeActive(String username) {
      Optional<RecoveryPasswordCode> existingCode = recoveryPasswordCodeRepository.findByUsername(username);
      return existingCode.isPresent() && !isRecoveryCodeExpired(existingCode.get());
   }

   private String generateRecoveryCode() {
      return String.join("", IntStream.generate(() -> random.nextInt(10))
            .limit(10)
            .mapToObj(String::valueOf)
            .toArray(String[]::new));
   }

   private boolean isRecoveryCodeExpired(RecoveryPasswordCode recoveryPasswordCode) {
      return recoveryPasswordCode.getExpirationTime() < System.currentTimeMillis();
   }

   @Override
   public void checkCode(CheckCodeRequest checkCodeRequest) {

      Optional<RecoveryPasswordCode> recoveryPasswordCode = recoveryPasswordCodeRepository
            .findByCode(checkCodeRequest.getCode());
      if (recoveryPasswordCode.isPresent()) {
         if (isRecoveryCodeExpired(recoveryPasswordCode.get())) {
            throw new RuntimeException("Recovery code expired");
         }
      } else {
         throw new RuntimeException("Recovery code not valid");
      }
   }

   @Override
   public void resetPasswordByCode(ResetPasswordByCodeRequest body) {

      Optional<RecoveryPasswordCode> recoveryPasswordCode = recoveryPasswordCodeRepository
            .findByCode(body.getCode());
      if (recoveryPasswordCode.isPresent()) {
         if (isRecoveryCodeExpired(recoveryPasswordCode.get())) {
            throw new RuntimeException("Recovery code expired");
         } else {
            User user = userRepository.findByUsername(recoveryPasswordCode.get().getUsername())
                  .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(body.getNewPassword(), user.getPassword())) {
               user.setPassword(passwordEncoder.encode(body.getNewPassword()));
               userRepository.save(user);
            } else {
               throw new RuntimeException("New password is the same as the old password");
            }
         }
      } else {
         throw new RuntimeException("Recovery code not valid");
      }
   }

}
