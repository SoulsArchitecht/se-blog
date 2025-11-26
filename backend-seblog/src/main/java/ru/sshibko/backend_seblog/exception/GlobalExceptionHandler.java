package ru.sshibko.backend_seblog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.prefs.BackingStoreException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e,
                                                                  WebRequest request) {
        ErrorResponse error = ErrorResponse.of(HttpStatus.NOT_FOUND.value(),
                e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error.toString());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e,
                                                                          WebRequest request) {
        ErrorResponse error = ErrorResponse.of(HttpStatus.CONFLICT.value(),
                e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e,
                                                                     WebRequest request) {
        ErrorResponse error = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(),
                e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e,
                                                                       WebRequest request) {
        ErrorResponse error = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(),
                e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e,
                                                                       WebRequest request) {
        ErrorResponse error = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(),
                e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            UsernameNotFoundException ex, WebRequest request
    ) {
        ErrorResponse error = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, WebRequest request) {
        ErrorResponse error = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An internal server error occurred.",
                List.of(e.getClass().getSimpleName()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
