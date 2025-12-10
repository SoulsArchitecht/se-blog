package ru.sshibko.backend_seblog.aop.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.sshibko.backend_seblog.aop.SlugValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SlugValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSlug {

    String message() default "Invalid slug format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
