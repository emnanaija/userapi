package io.github.emnanaija.userapi.service;

import io.github.emnanaija.userapi.dto.UserRequest;
import io.github.emnanaija.userapi.dto.UserResponse;
import io.github.emnanaija.userapi.entity.UserEntity;
import io.github.emnanaija.userapi.enums.Gender;
import io.github.emnanaija.userapi.exception.ResourceNotFoundException;
import io.github.emnanaija.userapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserResponse createUser(UserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Le corps de la requête est requis");
        }

        // Créer l'entité
        UserEntity entity = new UserEntity();
        entity.setUserName(request.getUsername());
        entity.setBirthDate(request.getBirthdate());
        entity.setCountry(request.getCountry());
        entity.setPhoneNumber(request.getPhone());

        // Gestion du genre
        if (request.getGender() != null && !request.getGender().isEmpty()) {
            try {
                entity.setGender(Gender.valueOf(request.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Gender invalide : " + request.getGender());
            }
        }

        // Sauvegarde dans la base
        UserEntity saved = userRepository.save(entity);

        // Conversion en DTO de sortie
        return toResponse(saved);
    }


    public UserResponse getUser(Long id) {
        Optional<UserEntity> opt = userRepository.findById(id);
        UserEntity entity = opt.orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return toResponse(entity);
    }


    private UserResponse toResponse(UserEntity entity) {
        String gender = entity.getGender() == null ? null : entity.getGender().name();
        return new UserResponse(
                entity.getId(),
                entity.getUserName(),
                entity.getBirthDate(),
                entity.getCountry(),
                entity.getPhoneNumber(),
                gender
        );
    }
}
