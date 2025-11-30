package io.github.emnanaija.userapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AdultFrenchResidentValidator.class)
public @interface AdultFrenchResident {
    String message() default "L'utilisateur doit être résident français et majeur";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

