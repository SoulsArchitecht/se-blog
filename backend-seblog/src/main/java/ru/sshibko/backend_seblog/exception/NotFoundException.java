package ru.sshibko.backend_seblog.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {

    public NotFoundException(ErrorCode errorCode, String resourceName, Object identifier) {
        super(errorCode.getCode(),
                HttpStatus.NOT_FOUND,
                String.format("%s not found", resourceName),
                String.format("%s with identifier '%s' not found", resourceName, identifier),
                identifier);
    }

    public NotFoundException(ErrorCode errorCode, String resourceName, Object identifier, String customMessage) {
        super(errorCode.getCode(),
                HttpStatus.NOT_FOUND,
                customMessage,
                String.format("%s with identifier '%s' not found", resourceName, identifier),
                identifier);
    }
}
