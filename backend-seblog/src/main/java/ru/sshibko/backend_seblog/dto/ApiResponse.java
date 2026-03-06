package ru.sshibko.backend_seblog.dto;

import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
public class ApiResponse<T> {

    boolean success;
    T data;
    String message;
    String errorCode;
    Integer httpStatus;

    public static <T> ApiResponse<T> success(T data, String message, HttpStatus httpStatus) {
        return new ApiResponse<>(true, data, message,  null, httpStatus.value());
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return success(data, message, HttpStatus.OK);
    }

    public static ApiResponse<Void> success(String message) {
        return success(null, message, HttpStatus.OK);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode,
                                           String errorMessage, HttpStatus httpStatus) {
        return new ApiResponse<>(false, null, message, errorCode, httpStatus.value());
    }
}
