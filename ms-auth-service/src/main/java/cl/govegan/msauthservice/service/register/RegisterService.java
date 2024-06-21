package cl.govegan.msauthservice.service.register;

import cl.govegan.msauthservice.model.User;
import cl.govegan.msauthservice.web.request.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface RegisterService {

    User register (RegisterRequest registerRequest);
}
