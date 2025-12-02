package io.github.emnanaija.userapi.validation;

import io.github.emnanaija.userapi.dto.UserRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AdultFrenchResidentValidator implements ConstraintValidator<AdultFrenchResident, UserRequest> {
    @Override
    public boolean isValid(UserRequest req, ConstraintValidatorContext ctx) {
        if (req == null) return true;
        boolean ok = true;
        // check country is exactly "France" or ISO code "FR" (case insensitive)
        String country = req.getCountry();
        if (country == null) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("country obligatoire")
                    .addPropertyNode("country").addConstraintViolation();
            ok = false;
        } else {
            String countryLower = country.toLowerCase().trim();
            // Accept "france" or "fr" (ISO code)
            if (!countryLower.equals("france") && !countryLower.equals("fr")) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate("Seuls les résidents français peuvent s'inscrire")
                        .addPropertyNode("country").addConstraintViolation();
                ok = false;
            }
        }
        if (req.getBirthdate() == null) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("birthdate requis")
                    .addPropertyNode("birthdate").addConstraintViolation();
            return false;
        }
        LocalDate today = LocalDate.now();
        long age = ChronoUnit.YEARS.between(req.getBirthdate(), today);
        if (age < 18) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("L'utilisateur doit être majeur (>=18 ans)")
                    .addPropertyNode("birthdate").addConstraintViolation();
            ok = false;
        }
        return ok;
    }
}
