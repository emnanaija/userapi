package io.github.emnanaija.userapi.dto;


import io.github.emnanaija.userapi.validation.AdultFrenchResident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data                   // génère getters, setters, toString, equals, hashCode
@NoArgsConstructor      // constructeur sans arguments
@AllArgsConstructor     // constructeur avec tous les arguments
@AdultFrenchResident
public class UserRequest {

    @NotBlank(message = "username obligatoire")
    private String username;

    @NotNull(message = "birthdate obligatoire")
    @Past(message = "birthdate doit être dans le passé")
    private LocalDate birthdate;

    @NotBlank(message = "country obligatoire")
    private String country;

    @Pattern(regexp = "^(?:\\+?\\d{8,15})?$", message = "phone invalide")
    private String phone;

    private String gender;
}
