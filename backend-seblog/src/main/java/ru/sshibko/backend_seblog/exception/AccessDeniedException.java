package ru.sshibko.backend_seblog.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException  extends BaseException {

    public AccessDeniedException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), HttpStatus.FORBIDDEN, message);
    }

    public AccessDeniedException(ErrorCode errorCode, String userMessage, String developerMessage) {
        super(errorCode.getCode(), HttpStatus.FORBIDDEN, userMessage, developerMessage);
    }
}
