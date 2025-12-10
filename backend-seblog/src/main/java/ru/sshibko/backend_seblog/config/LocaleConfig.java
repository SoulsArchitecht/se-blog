package ru.sshibko.backend_seblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    public enum AppLocale {
        EN("en", Locale.ENGLISH),
        RU("ru", new Locale("ru"));

        private final String code;
        private final Locale locale;

        AppLocale(String code, Locale locale) {
            this.code = code;
            this.locale = locale;
        }

        public String getCode() {
            return code;
        }

        public Locale getLocale() {
            return locale;
        }

        public static AppLocale fromCode(String code) {
            for (AppLocale locale : AppLocale.values()) {
                if (locale.code.equalsIgnoreCase(code) ) {
                    return locale;
                }
            }
            return EN;
        }

        public static AppLocale fromLocale(Locale locale) {
            String code = locale.getLanguage();
            return fromCode(code);
        }
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
