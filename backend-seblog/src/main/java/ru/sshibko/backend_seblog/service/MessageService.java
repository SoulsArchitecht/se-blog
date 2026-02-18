package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.exception.ErrorCode;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    public String getMessage(ErrorCode errorCode, Locale locale) {
        String key = toMessageKey(errorCode);
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (NoSuchMessageException e) {
            return errorCode.getDefaultMessage();
        }
    }

    private String toMessageKey(ErrorCode errorCode) {
        return "error." +
                errorCode.name().toLowerCase()
                        .replace('-', '.')
                        .replace("err.","");
    }
}
