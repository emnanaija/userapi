package io.github.emnanaija.userapi.service;

import io.github.emnanaija.userapi.dto.UserRequest;
import io.github.emnanaija.userapi.dto.UserResponse;
import io.github.emnanaija.userapi.entity.UserEntity;
import io.github.emnanaija.userapi.enums.Gender;
import io.github.emnanaija.userapi.exception.ResourceNotFoundException;
import io.github.emnanaija.userapi.repository.UserRepository;
import io.github.emnanaija.userapi.validation.AdultFrenchResident;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class UserServiceValidationTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository);
    private final Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();


    // Test 1 : genre invalide - Le service doit gérer cela

    @Test
    void shouldThrowWhenGenderInvalid() {
        UserRequest request = new UserRequest();
        request.setUsername("Jean");
        request.setBirthdate(LocalDate.of(1990, 1, 1));
        request.setCountry("France");
        request.setGender("INVALID"); // pas dans l'enum

        // Le service doit lancer une exception pour un genre invalide
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
    }


    // Test 2 : utilisateur mineur (<18 ans) - Test de l'annotation @AdultFrenchResident

    @Test
    void shouldThrowWhenUserUnder18() {
        UserRequest request = new UserRequest();
        request.setUsername("Julie");
        request.setBirthdate(LocalDate.now().minusYears(17)); // moins de 18 ans
        request.setCountry("France");
        request.setGender("FEMALE");

        // Vérifier que la validation Jakarta échoue (l'annotation @AdultFrenchResident est appliquée)
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "La validation @AdultFrenchResident devrait échouer pour un utilisateur mineur");
        
        // Vérifier que c'est bien l'annotation @AdultFrenchResident qui a été déclenchée
        boolean hasAdultFrenchResidentViolation = violations.stream()
                .anyMatch(v -> v.getConstraintDescriptor().getAnnotation().annotationType() == AdultFrenchResident.class);
        assertTrue(hasAdultFrenchResidentViolation, "La violation devrait provenir de @AdultFrenchResident");
        
        // Vérifier que le message d'erreur contient l'information sur l'âge
        String violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        assertTrue(violationMessages.contains("majeur") || violationMessages.contains("18"), 
                "Le message devrait mentionner l'âge minimum");
    }


    // Test 3 : utilisateur non français - Test de l'annotation @AdultFrenchResident

    @Test
    void shouldThrowWhenNotFrench() {
        UserRequest request = new UserRequest();
        request.setUsername("Carlos");
        request.setBirthdate(LocalDate.of(1990, 1, 1));
        request.setCountry("Spain"); // pas France
        request.setGender("MALE");

        // Vérifier que la validation Jakarta échoue (l'annotation @AdultFrenchResident est appliquée)
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "La validation @AdultFrenchResident devrait échouer pour un utilisateur non français");
        
        // Vérifier que c'est bien l'annotation @AdultFrenchResident qui a été déclenchée
        boolean hasAdultFrenchResidentViolation = violations.stream()
                .anyMatch(v -> v.getConstraintDescriptor().getAnnotation().annotationType() == AdultFrenchResident.class);
        assertTrue(hasAdultFrenchResidentViolation, "La violation devrait provenir de @AdultFrenchResident");
        
        // Vérifier que le message d'erreur concerne le pays
        String violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        assertTrue(violationMessages.contains("français") || violationMessages.contains("France"), 
                "Le message devrait mentionner la France");
    }


    // Test 4 : date future - Validation Jakarta

    @Test
    void shouldThrowWhenBirthdateInFuture() {
        UserRequest request = new UserRequest();
        request.setUsername("Alice");
        request.setBirthdate(LocalDate.now().plusDays(1)); // date future
        request.setCountry("France");
        request.setGender("FEMALE");

        // Vérifier que la validation Jakarta échoue
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "La validation devrait échouer pour une date future");
        
        // Vérifier que le message d'erreur concerne la date passée
        String violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        assertTrue(violationMessages.contains("passé") || violationMessages.contains("past"), 
                "Le message devrait mentionner que la date doit être dans le passé");
    }

    @Test
    void shouldCreateUserSuccessfully() {
        UserRequest request = new UserRequest();
        request.setUsername("Jean");
        request.setBirthdate(LocalDate.of(1990, 1, 1));
        request.setCountry("France");
        request.setGender("MALE");

        // Vérifier que la validation Jakarta passe (y compris @AdultFrenchResident)
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "La validation @AdultFrenchResident devrait passer pour un utilisateur valide");

        // Simule la sauvegarde en base et l'ID généré
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            entity.setId(1L); // Simule l'ID auto-généré
            return entity;
        });

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Jean", response.getUsername());
        assertEquals("MALE", response.getGender());
        assertEquals("France", response.getCountry());
        assertEquals(LocalDate.of(1990, 1, 1), response.getBirthdate());
    }

    // -----------------------------
    // Tests pour getUser(Long id)
    // -----------------------------

    @Test
    void shouldGetUserByIdSuccessfully() {
        // Créer un utilisateur mocké
        UserEntity mockEntity = new UserEntity();
        mockEntity.setId(1L);
        mockEntity.setUserName("Jean");
        mockEntity.setBirthDate(LocalDate.of(1990, 1, 1));
        mockEntity.setCountry("France");
        mockEntity.setPhoneNumber("0123456789");
        mockEntity.setGender(Gender.MALE);

        // Mock du repository
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockEntity));

        // Appel du service
        UserResponse response = userService.getUser(1L);

        // Vérifications
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Jean", response.getUsername());
        assertEquals(LocalDate.of(1990, 1, 1), response.getBirthdate());
        assertEquals("France", response.getCountry());
        assertEquals("0123456789", response.getPhone());
        assertEquals("MALE", response.getGender());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
        // Mock du repository pour retourner un Optional vide
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Vérifier que l'exception est lancée
        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(999L));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWithCorrectMessage() {
        // Mock du repository pour retourner un Optional vide
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Vérifier que l'exception est lancée avec le bon message
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUser(999L)
        );
        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }

}
