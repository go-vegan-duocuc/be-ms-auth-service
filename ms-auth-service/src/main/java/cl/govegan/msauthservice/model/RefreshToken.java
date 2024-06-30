package cl.govegan.msauthservice.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "refreshTokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

   @Id
   private String id;

   private String token;
   private String username;
   private Instant expiration;

}
