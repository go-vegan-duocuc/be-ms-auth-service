package cl.govegan.msauthservice.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    /* User data */
    @NotNull
    @Size(min = 4, max = 20)
    String username;

    @NotNull
    @Size(min = 8)
    String password;

    @NotNull
    @Email
    String email;
}