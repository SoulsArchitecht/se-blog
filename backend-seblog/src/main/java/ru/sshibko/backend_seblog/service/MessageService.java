package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.exception.ErrorCode;
import ru.sshibko.backend_seblog.exception.SuccessCode;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    public String getSuccessMessage(SuccessCode code, Locale locale) {
        String key = toSuccessMessageKey(code);
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (NoSuchMessageException e) {
            return code.getDefaultMessage();
        }
    }

    public String getErrorMessage(ErrorCode code, Locale locale) {
        String key = toErrorMessageKey(code);
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (NoSuchMessageException e) {
            return code.getDefaultMessage();
        }
    }

    private String toSuccessMessageKey(SuccessCode code) {
        return "success." +
                code.name().toLowerCase()
                        .replace('_', '.');
    }

    private String toErrorMessageKey(ErrorCode code) {
        return "error." +
                code.name().toLowerCase()
                        .replace('_', '.');
    }
}
