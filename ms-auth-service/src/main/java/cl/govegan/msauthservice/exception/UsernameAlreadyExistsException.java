package cl.govegan.msauthservice.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException (String message) {
        super(message);
    }
}
