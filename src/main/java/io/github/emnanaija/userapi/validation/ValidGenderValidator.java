package io.github.emnanaija.userapi.validation;

import io.github.emnanaija.userapi.enums.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validateur pour vérifier qu'une String correspond à une valeur valide de l'enum Gender.
 * Accepte null car le champ est optionnel.
 */
public class ValidGenderValidator implements ConstraintValidator<ValidGender, String> {
    
    @Override
    public void initialize(ValidGender constraintAnnotation) {
        // Pas d'initialisation nécessaire
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Le champ est optionnel, donc null est accepté
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        
        // Vérifier si la valeur correspond à un enum Gender valide (insensible à la casse)
        try {
            Gender.valueOf(value.toUpperCase().trim());
            return true;
        } catch (IllegalArgumentException e) {
            // Construire un message d'erreur avec les valeurs acceptées
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Gender invalide : '" + value + "'. Valeurs acceptées : MALE, FEMALE, OTHER"
            ).addConstraintViolation();
            return false;
        }
    }
}

