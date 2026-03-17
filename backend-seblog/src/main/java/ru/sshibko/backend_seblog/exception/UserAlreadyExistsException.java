package ru.sshibko.backend_seblog.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException {


    public UserAlreadyExistsException(ErrorCode errorCode, String userMessage) {
        super(errorCode.getCode(), HttpStatus.FORBIDDEN, userMessage);
    }

    public UserAlreadyExistsException(ErrorCode errorCode, String userMessage, String developerMessage) {
        super(errorCode.getCode(), HttpStatus.FORBIDDEN, userMessage, developerMessage);
    }
}
