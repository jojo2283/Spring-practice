package com.example.demo.controller;

import com.example.demo.model.UserEntity;
import com.example.demo.model.request.JwtRequest;
import com.example.demo.model.request.RegistrationRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@AutoConfigureEmbeddedDatabase(
        provider = ZONKY,
        refresh = AutoConfigureEmbeddedDatabase.RefreshMode.BEFORE_EACH_TEST_METHOD,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)

@ActiveProfiles("test-security")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testRegister() throws Exception {
        String username = "user";
        String email = "test@mail.ru";
        String password = "password";
        RegistrationRequest regRequest = new RegistrationRequest(username, email, password);


        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(regRequest)))
                .andExpect(status().isOk());


        assertThat(userRepository.count()).isEqualTo(1);

        UserEntity user = userRepository.findAll().get(0);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
    }

    @Sql("/sql/create_user.sql")
    @Test
    void testLogin() throws Exception {

        JwtRequest authRequest = new JwtRequest("username", "password");


        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());


    }

    @Sql("/sql/create_user.sql")
    @Test
    @DisplayName("User already exist")
    void getUser_ShouldReturnUseralreadyExist() throws Exception {
        String username = "username";
        String email = "test@mail.ru";
        String password = "password";
        RegistrationRequest regRequest = new RegistrationRequest(username, email, password);


        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(regRequest)))
                .andExpect(status().isConflict());


        assertThat(userRepository.count()).isEqualTo(1);

        UserEntity user = userRepository.findAll().get(0);
        assertThat(user.getUsername()).isEqualTo(username);

    }
}
