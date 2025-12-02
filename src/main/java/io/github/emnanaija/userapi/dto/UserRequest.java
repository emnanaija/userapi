package io.github.emnanaija.userapi.dto;


import io.github.emnanaija.userapi.validation.AdultFrenchResident;
import io.github.emnanaija.userapi.validation.ValidGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@AdultFrenchResident
public class UserRequest {

    @NotBlank(message = "username obligatoire")
    private String username;

    @NotNull(message = "birthdate obligatoire")
    @Past(message = "birthdate doit être dans le passé")
    private LocalDate birthdate;

    @NotBlank(message = "country obligatoire")
    private String country;

    @Pattern(regexp = "^(0\\d{9}|\\+33\\d{9})$", message = "Numéro français invalide")
    private String phone;

    @ValidGender
    private String gender;
}
