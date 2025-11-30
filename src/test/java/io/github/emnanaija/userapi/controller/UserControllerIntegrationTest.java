package io.github.emnanaija.userapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.emnanaija.userapi.dto.UserRequest;
import io.github.emnanaija.userapi.dto.UserResponse;
import io.github.emnanaija.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    // -----------------------------
    // Test 1 : Création utilisateur valide
    // -----------------------------
    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("JeanDupont");
        request.setBirthdate(LocalDate.of(1990, 5, 15));
        request.setCountry("France");
        request.setGender("MALE");

        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("JeanDupont"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UserResponse response = objectMapper.readValue(responseBody, UserResponse.class);
        assertNotNull(response.getId());
        assertEquals("JeanDupont", response.getUsername());
    }

    // -----------------------------
    // Test 2 : Récupération utilisateur par ID
    // -----------------------------
    @Test
    void shouldGetUserById() throws Exception {
        // Créer un utilisateur d'abord
        UserRequest createRequest = new UserRequest();
        createRequest.setUsername("MarieMartin");
        createRequest.setBirthdate(LocalDate.of(1985, 3, 20));
        createRequest.setCountry("France");
        createRequest.setGender("FEMALE");

        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponse createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), UserResponse.class);
        Long userId = createdUser.getId();

        // Récupérer l'utilisateur
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("MarieMartin"));
    }

    // -----------------------------
    // Test 3 : Utilisateur non trouvé
    // -----------------------------
    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    // -----------------------------
    // Test 4 : Validation - utilisateur mineur
    // -----------------------------
    @Test
    void shouldRejectUserUnder18() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("PetitJean");
        request.setBirthdate(LocalDate.now().minusYears(17));
        request.setCountry("France");
        request.setGender("MALE");

        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("majeur") || responseBody.contains("18"));
    }

    // -----------------------------
    // Test 5 : Validation - utilisateur non français
    // -----------------------------
    @Test
    void shouldRejectNonFrenchUser() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("Carlos");
        request.setBirthdate(LocalDate.of(1990, 1, 1));
        request.setCountry("Spain");
        request.setGender("MALE");

        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("français") || responseBody.contains("France"));
    }

    // -----------------------------
    // Test 6 : Validation - date future
    // -----------------------------
    @Test
    void shouldRejectFutureBirthdate() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("Alice");
        request.setBirthdate(LocalDate.now().plusDays(1));
        request.setCountry("France");
        request.setGender("FEMALE");

        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("passé") || responseBody.contains("birthdate"));
    }
}
