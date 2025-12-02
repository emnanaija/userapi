package io.github.emnanaija.userapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation de validation pour vérifier qu'une String correspond à une valeur valide de l'enum Gender.
 * Accepte null (car le champ est optionnel).
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidGenderValidator.class)
public @interface ValidGender {
    String message() default "Gender invalide. Valeurs acceptées : MALE, FEMALE, OTHER";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

