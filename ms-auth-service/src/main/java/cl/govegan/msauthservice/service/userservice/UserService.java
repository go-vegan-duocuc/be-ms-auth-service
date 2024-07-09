package cl.govegan.msauthservice.service.userservice;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.web.request.NewEmailRequest;
import cl.govegan.msauthservice.web.request.NewPasswordRequest;

@Service
public interface UserService {

   public User getUserDetails(Authentication authentication);

   public void updatePassword(NewPasswordRequest newPasswordRequest, Authentication authentication);

   public String updateEmail(NewEmailRequest newEmailRequest, Authentication authentication);

   public void deleteAccount(Authentication authentication);

   public User getUserByEmail(String email);

}
