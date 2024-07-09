package cl.govegan.msauthservice.web.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewPasswordRequest {

   private String oldPassword;
   private String newPassword;

}
