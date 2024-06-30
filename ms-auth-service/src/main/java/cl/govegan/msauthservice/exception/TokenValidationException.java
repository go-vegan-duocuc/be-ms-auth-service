package cl.govegan.msauthservice.exception;

public class TokenValidationException extends RuntimeException{
      public TokenValidationException(String message){
         super(message);
      }
}
