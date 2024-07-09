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

   public void sendEmail(String to, Context context) throws MessagingException {

      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

      String htmlContent = templateEngine.process("password-reset-email", context);

      helper.setFrom("hello.govegan@zohomail.com");
      helper.setTo(to);
      helper.setSubject("Reset your password");
      helper.setText(htmlContent, true);

      mailSender.send(mimeMessage);
   }

}