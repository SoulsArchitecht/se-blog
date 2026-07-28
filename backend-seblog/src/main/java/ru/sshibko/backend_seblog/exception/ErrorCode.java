package ru.sshibko.backend_seblog.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Общие ошибки
    INTERNAL_SERVER_ERROR("ERR-0001", "Внутренняя ошибка сервера"),
    VALIDATION_ERROR("ERR-0002", "Ошибка валидации"),
    ACCESS_DENIED("ERR-0003", "Доступ запрещен"),
    UNAUTHORIZED("ERR-0004", "Требуется авторизация"),

    // Ресурсы не найдены
    POST_NOT_FOUND("ERR-0101", "Пост не найден"),
    POST_TYPE_NOT_FOUND("ERR-0102", "Тип поста не найден"),
    TAG_NOT_FOUND("ERR-0103", "Тег не найден"),
    COMMENT_NOT_FOUND("ERR-0104", "Комментарий не найден"),
    USER_NOT_FOUND("ERR-0105", "Пользователь не найден"),
    USER_PROFILE_NOT_FOUND("ERR-0106", "Профиль пользователя не найден"),

    // Валидация данных
    INVALID_SLUG_FORMAT("ERR-0201", "Некорректный формат slug"),
    DUPLICATE_SLUG("ERR-0202", "Slug уже используется"),
    DUPLICATE_TAG_NAME("ERR-0203", "Тег с таким названием уже существует"),
    DUPLICATE_POST_TYPE("ERR-0204", "Тип поста уже существует"),
    INVALID_POST_STATUS("ERR-0205", "Некорректный статус поста"),
    INVALID_VOTE_TYPE("ERR-0206", "Некорректный тип голоса"),

    // Бизнес-правила
    CANNOT_VOTE_OWN_POST("ERR-0301", "Нельзя голосовать за свой собственный пост"),
    CANNOT_VOTE_OWN_COMMENT("ERR-0302", "Нельзя голосовать за свой собственный комментарий"),
    CANNOT_EDIT_DELETED_COMMENT("ERR-0303", "Нельзя редактировать удаленный комментарий"),
    CANNOT_PUBLISH_ALREADY_PUBLISHED("ERR-0304", "Пост уже опубликован"),

    // Конфликты
    COMMENT_PARENT_MISMATCH("ERR-0401", "Родительский комментарий принадлежит другому посту"),
    VOTE_ALREADY_EXISTS("ERR-0402", "Голос уже существует"),
    VOTE_NOT_FOUND("ERR-0403", "Голос не найден"),
    USER_ALREADY_EXISTS("ERR-0404", "Пользователь уже существует");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
