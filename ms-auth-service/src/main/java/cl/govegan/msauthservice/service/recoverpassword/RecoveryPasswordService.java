package cl.govegan.msauthservice.service.recoverpassword;

import org.springframework.stereotype.Service;

import cl.govegan.msauthservice.model.RecoveryPasswordCode;
import cl.govegan.msauthservice.web.request.CheckCodeRequest;
import cl.govegan.msauthservice.web.request.ResetPasswordByCodeRequest;
import jakarta.mail.MessagingException;

@Service
public interface RecoveryPasswordService {

   public RecoveryPasswordCode sendRecoveryCodeByEmail(String email) throws MessagingException;

   public void checkCode(CheckCodeRequest checkCodeRequest);

   public void resetPasswordByCode(ResetPasswordByCodeRequest body);
}
