package cl.govegan.msauthservice.exception;

public class DatabaseErrorException extends RuntimeException {
    public DatabaseErrorException (String message) {
        super(message);
    }
}