package ru.sshibko.backend_seblog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.sshibko.backend_seblog.interceptor.RequestIdInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestIdInterceptor requestIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry  registry) {
        registry.addInterceptor(requestIdInterceptor)
                .addPathPatterns("api/v1/**");
    }
}
