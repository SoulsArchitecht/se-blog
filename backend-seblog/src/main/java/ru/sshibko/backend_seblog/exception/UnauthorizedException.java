package ru.sshibko.backend_seblog.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), HttpStatus.UNAUTHORIZED, message);
    }

    public UnauthorizedException(ErrorCode errorCode, String userMessage, String developerMessage) {
        super(errorCode.getCode(), HttpStatus.UNAUTHORIZED, userMessage, developerMessage);
    }
}
