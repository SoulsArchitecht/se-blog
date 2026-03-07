package ru.sshibko.backend_seblog.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String userMessage;
    private final String developerMessage;
    private final Object details;

    protected BaseException(String errorCode, HttpStatus httpStatus, String userMessage,
                            String developerMessage, Object details) {
        super(developerMessage != null ? developerMessage : userMessage);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
        this.developerMessage = developerMessage;
        this.details = details;
    }

    protected BaseException(String errorCode, HttpStatus httpStatus, String message) {
        this(errorCode, httpStatus, message, message, null);
    }

    protected BaseException(String errorCode, HttpStatus httpStatus, String userMessage, String developerMessage) {
        this(errorCode, httpStatus, userMessage, developerMessage, null);
    }
}
