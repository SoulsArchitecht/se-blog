package ru.sshibko.backend_seblog.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {

    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), HttpStatus.BAD_REQUEST, message);
    }

    public ValidationException(ErrorCode errorCode, String userMessage, String developerMessage) {
        super(errorCode.getCode(), HttpStatus.BAD_REQUEST, userMessage, developerMessage);
    }

    public ValidationException(ErrorCode errorCode, String message, Object details) {
        super(errorCode.getCode(), HttpStatus.BAD_REQUEST, message, message, details);
    }
}
