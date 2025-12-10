package ru.sshibko.backend_seblog.config;

public class AppConstants {

    // Пагинация
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // Длины полей
    public static final int MAX_TAG_NAME_LENGTH = 50;
    public static final int MAX_POST_TITLE_LENGTH = 200;
    public static final int MAX_SLUG_LENGTH = 100;

    // Регулярные выражения
    public static final String SLUG_PATTERN = "^[a-z0-9]+(?:-[a-z0-9]+)*$";

    private AppConstants() {}
}
