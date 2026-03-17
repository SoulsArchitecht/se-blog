package ru.sshibko.backend_seblog.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private String generateRequestId() {
        return "req-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "";
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e, WebRequest request) {
        String requestId = generateRequestId();
        String path = getRequestPath(request);

        log.error("BaseException [{}] at path {}: {} - {}",
                requestId, path, e.getErrorCode(), e.getDeveloperMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.fromException(e, path, requestId);
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e, WebRequest request) {
        String requestId = generateRequestId();
        String path = getRequestPath(request);

        log.warn("NotFoundException [{}] at path {} : {}",
                requestId, path, e.getDeveloperMessage());

        ErrorResponse errorResponse = ErrorResponse.fromException(e, path, requestId);
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException e, WebRequest request) {
        String requestId = generateRequestId();
        String path = getRequestPath(request);

        log.warn("UserAlreadyExistsException [{}] at path {}: {}",
                requestId, path, e.getDeveloperMessage());

        ErrorResponse errorResponse = ErrorResponse.fromException(e, path, requestId);
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(
            InvalidTokenException e, WebRequest request) {
        String requestId = generateRequestId();
        String path = getRequestPath(request);

        log.error("InvalidTokenException [{}] at path {}: {}",
                requestId, path, e.getDeveloperMessage());

        ErrorResponse errorResponse = ErrorResponse.fromException(e, path, requestId);
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException e, WebRequest request) {
        String requestId = generateRequestId();
        String path = getRequestPath(request);

        log.error("AuthenticationException [{}] at path {}: {}",
                requestId, path, e.getDeveloperMessage());

        ErrorResponse errorResponse = ErrorResponse.fromException(e, path, requestId);
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e, WebRequest request) {
        String requestId = generateRequestId();
        String path = getRequestPath(request);

        log.warn("ValidationException [{}] at path {}: {}",
                requestId, path, e.getDeveloperMessage());

        ErrorResponse errorResponse = ErrorResponse.fromException(e, path, requestId);
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, WebRequest request) {

        String requestId = generateRequestId();
        String path = getRequestPath(request);

        log.warn("BusinessException [{}] at path {}: {}",
                requestId, path, ex.getDeveloperMessage());

        ErrorResponse errorResponse = ErrorResponse.fromException(ex, path, requestId);
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        String requestId = generateRequestId();
        String path = getRequestPath(request);

        log.warn("AccessDeniedException [{}] at path {}: {}",
                requestId, path, e.getDeveloperMessage());

        ErrorResponse errorResponse = ErrorResponse.fromMessage(
                ErrorCode.ACCESS_DENIED.getCode(),
                HttpStatus.FORBIDDEN.value(),
                getLocalizedMessage("error.access.denied"),
                e.getMessage(),
                path,
                requestId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({BadRequestException.class, InsufficientAuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            Exception e, WebRequest request) {
        String requestId = generateRequestId();
        String path = getRequestPath(request);

        log.warn("AuthenticationException [{}] at path {}: {}",
                requestId, path, e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.fromMessage(
                ErrorCode.UNAUTHORIZED.getCode(),
                HttpStatus.UNAUTHORIZED.value(),
                getLocalizedMessage("error.unauthorized"),
                e.getMessage(),
                path,
                requestId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(
             MethodArgumentNotValidException e, WebRequest request) {
        String requestId = generateRequestId();
        String path = getRequestPath(request);

        Map<String, String> fieldErrors = new HashMap<>();
        Map<String, String> globalErrors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach((fieldError) -> {
            String fieldName = fieldError.getField();
            String errorMessage = getValidationErrorMessage(fieldError);
            fieldErrors.put(fieldName, errorMessage);
        });

        e.getBindingResult().getGlobalErrors().forEach((globalError) -> {
            String objectName = globalError.getObjectName();
            String errorMessage = globalError.getDefaultMessage();
            globalErrors.put(objectName, errorMessage != null ? errorMessage : "Validation error");
        });

        log.warn("MethodArgumentNotValidException [{}] at path {}: {} field errors, {} global errors",
                requestId, path, fieldErrors.size(), globalErrors.size());

        ValidationErrorResponse errorResponse = ValidationErrorResponse.create(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_ERROR.getCode(),
                getLocalizedMessage("error.validation"),
                path,
                fieldErrors,
                globalErrors,
                requestId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
     }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(
            Exception ex, WebRequest request) {

        String requestId = generateRequestId();
        String path = getRequestPath(request);

        log.error("UncaughtException [{}] at path {}: {}",
                requestId, path, ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.fromMessage(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                getLocalizedMessage("error.internal.server"),
                "An unexpected error occurred: " + ex.getMessage(),
                path,
                requestId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getLocalizedMessage(String messageKey) {
        try {
            return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return messageKey;
        }
    }

    private String getValidationErrorMessage(FieldError fieldError) {
        if (fieldError.getDefaultMessage() != null) {
            return fieldError.getDefaultMessage();
        }

        String messageKey = String.format("error.validation.%s.%s",
                fieldError.getField(), fieldError.getCode());
        try {
            return messageSource.getMessage(messageKey,
                    fieldError.getArguments(),
                    LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return fieldError.getDefaultMessage() != null ?
                    fieldError.getDefaultMessage() : "Invalid value";
        }
    }

/*    @ExceptionHandler(ResourceNotFoundException.class)
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

*//*    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e,
                                                                   WebRequest request) {
        ErrorResponse error = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }*//*

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, WebRequest request) {
        ErrorResponse error = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An internal server error occurred.",
                List.of(e.getClass().getSimpleName()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }*/
}
