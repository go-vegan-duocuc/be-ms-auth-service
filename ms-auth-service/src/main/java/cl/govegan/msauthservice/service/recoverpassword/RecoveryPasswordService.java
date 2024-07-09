package cl.govegan.msauthservice.service.recoverpassword;

import org.springframework.stereotype.Service;

import cl.govegan.msauthservice.model.RecoveryPasswordCode;

@Service
public interface RecoveryPasswordService {

   public RecoveryPasswordCode sendRecoveryCodeByEmail(String email);

   public void deleteRecoverPasswordCode(String code);
}
