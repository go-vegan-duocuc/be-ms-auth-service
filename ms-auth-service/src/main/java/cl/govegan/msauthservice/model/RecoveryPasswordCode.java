package cl.govegan.msauthservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Document(collection = "recoveryPasswordCodes")

public class RecoveryPasswordCode {

      @Id
      private String id;
      private String code;
      private String username;
      private boolean used;
      private Long expirationTime;
      private boolean expired;
}
