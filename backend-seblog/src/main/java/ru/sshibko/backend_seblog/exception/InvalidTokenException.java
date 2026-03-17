package ru.sshibko.backend_seblog.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException {


    public InvalidTokenException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), HttpStatus.NETWORK_AUTHENTICATION_REQUIRED, message);
    }

    public InvalidTokenException(ErrorCode errorCode, String userMessage, String developerMessage) {
        super(errorCode.getCode(), HttpStatus.NETWORK_AUTHENTICATION_REQUIRED, userMessage, developerMessage);
    }
}
