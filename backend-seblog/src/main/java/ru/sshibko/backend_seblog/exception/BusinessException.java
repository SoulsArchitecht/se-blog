package ru.sshibko.backend_seblog.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends BaseException {

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), HttpStatus.CONFLICT, message);
    }

    public BusinessException(ErrorCode errorCode, String userMessage, String developerMessage) {
        super(errorCode.getCode(), HttpStatus.CONFLICT, userMessage, developerMessage);
    }

    public BusinessException(ErrorCode errorCode, String message, Object details) {
        super(errorCode.getCode(), HttpStatus.CONFLICT, message, message, details);
    }
}
