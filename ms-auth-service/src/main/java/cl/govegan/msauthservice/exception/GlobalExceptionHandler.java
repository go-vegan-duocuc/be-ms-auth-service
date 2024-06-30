package cl.govegan.msauthservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import cl.govegan.msauthservice.web.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
   @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<Object> handleAuthenticationServiceException(AuthenticationServiceException ex, WebRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
