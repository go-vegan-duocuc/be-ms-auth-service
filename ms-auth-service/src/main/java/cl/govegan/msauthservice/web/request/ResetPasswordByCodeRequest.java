package cl.govegan.msauthservice.web.request;

import lombok.Data;

@Data
public class ResetPasswordByCodeRequest {

   private String code;
   private String newPassword;
   
}
