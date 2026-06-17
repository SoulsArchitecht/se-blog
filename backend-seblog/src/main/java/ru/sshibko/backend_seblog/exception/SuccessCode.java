package ru.sshibko.backend_seblog.exception;

import lombok.Getter;

@Getter
public enum SuccessCode {
    // Пользователь
    USER_REGISTERED("MSG-0101", "Пользователь успешно зарегистрирован"),
    USER_LOGGED_IN("MSG-0102", "Успешный вход"),
    USER_LOGGED_OUT("MSG-0103", "Выход выполнен"),
    USER_PROFILE_UPDATED("MSG-0104", "Профиль обновлён"),
    USER_PASSWORD_CHANGED("MSG-0105", "Пароль успешно изменён"),
    TOKEN_REFRESHED("MSG-0106", "Токен успешно обновлен"),
    USER_PROFILE_RECEIVED("MSG-0107", "Профиль пользователя получен"),
    AVATAR_UPLOADED("MSG-0108", "Аватар успешно загружен"),

    // Посты
    POST_CREATED("MSG-0201", "Пост успешно создан"),
    POST_UPDATED("MSG-0202", "Пост обновлён"),
    POST_DELETED("MSG-0203", "Пост удалён"),
    POST_PUBLISHED("MSG-0204", "Пост опубликован"),
    POST_UNPUBLISHED("MSG-0205", "Публикация поста отменена"),
    POST_BY_SLUG_RECEIVED("MSG-0206", "Посты по слагу получены"),
    POST_BY_ID_RECEIVED("MSG_0207", "Пост получен по ID"),
    POSTS_PUBLISHED_LIST_RECEIVED("MSG-0208", "Опубликованные посты получены"),
    POSTS_BY_TAG_RECEIVED("MSG-0209", "Посты по указанному тэгу получены"),
    POSTS_BY_TYPE_RECEIVED("MSG-0210", "Посты с указанным типом получены"),
    POST_VOTED("MSG-0211", "Голос за пост отдан"),
    POST_VOTE_REMOVED("MSG-0212", "Голос за пост удален"),
    POST_VOTE_STATS_RECEIVED("MSG-0213", "Статистика за пост получена"),

    // Комментарии
    COMMENT_ADDED("MSG-0301", "Комментарий добавлен"),
    COMMENT_UPDATED("MSG-0302", "Комментарий обновлён"),
    COMMENT_DELETED("MSG-0303", "Комментарий удалён"),
    COMMENTS_RECEIVED("MSG-0304", "Комментарии к посту получены"),
    COMMENT_VOTED("MSG-0305", "Голос за комментарий отдан"),
    COMMENT_VOTE_REMOVED("MSG-0306", "Голос за комментарий удален"),
    COMMENT_VOTE_STATS_RECEIVED("MSG-0307", "Статистика за комментарий получена"),

    // Голосование
    VOTE_CAST("MSG-0401", "Голос учтён"),
    VOTE_REMOVED("MSG-0402", "Голос отменён"),

    // Теги / типы
    TAG_CREATED("MSG-0501", "Тег создан"),
    POST_TYPE_CREATED("MSG-0502", "Тип поста создан"),

    // Общие
    OPERATION_SUCCESSFUL("MSG-0001", "Операция выполнена успешно"),
    EMAIL_SENT("MSG-0002", "Письмо отправлено"),
    SUBSCRIPTION_UPDATED("MSG-0003", "Подписка обновлена");

    private final String code;
    private final String defaultMessage;

    SuccessCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
