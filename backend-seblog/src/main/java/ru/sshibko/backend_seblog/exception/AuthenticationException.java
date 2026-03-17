package ru.sshibko.backend_seblog.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException {

    public AuthenticationException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), HttpStatus.UNAUTHORIZED, message);
    }

    public AuthenticationException(ErrorCode errorCode, String userMessage, String developerMessage) {
        super(errorCode.getCode(), HttpStatus.UNAUTHORIZED, userMessage, developerMessage);
    }
}
