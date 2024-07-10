package cl.govegan.msauthservice.service.email;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

   private final JavaMailSender mailSender;
   private final TemplateEngine templateEngine;

   public void sendRecoveryPasswordEmail(String to, Context context) throws MessagingException {

      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

      String htmlContent = templateEngine.process("password-reset-email", context);

      helper.setFrom("hello.govegan@zohomail.com");
      helper.setTo(to);
      helper.setSubject("Reset your password");
      helper.setText(htmlContent, true);

      mailSender.send(mimeMessage);
   }

   public void sendWelcomeEmail(String to, String username) throws MessagingException {
      
      Context context = new Context();
      context.setVariable("username", username);
      
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
      
      String htmlContent = templateEngine.process("welcome-email", context);
      
      helper.setFrom("hello.govegan@zohomail.com");
      helper.setTo(to);
      helper.setSubject("Welcome to GoVegan");
      helper.setText(htmlContent, true);
      
      mailSender.send(mimeMessage);
   }

}