package com.example.demo.controller;


import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(
        provider = ZONKY,
        refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
})
@ActiveProfiles("test")
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName(value = "Create Author")
    public void createAuthor_ShouldReturnAuthor() throws Exception {
        //given
        authorRepository.deleteAll();
        assertThat(authorRepository.count()).isZero();
        String authorName = "John Doe";
        Author author = new Author(null, authorName, "smth", new ArrayList<>());


        //when
        mockMvc.perform(post("/api/authors")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(authorName))
                .andExpect(jsonPath("$.biography").value("smth"));
        //then
        assertThat(authorRepository.count()).isEqualTo(1);

        Author createdAuthor = authorRepository.findAll().get(0);
        assertThat(createdAuthor.getName()).isEqualTo("John Doe");
        assertThat(createdAuthor.getBiography()).isEqualTo("smth");

    }

    @Test
    @DisplayName(value = "Create invalid Author")
    public void createAuthor_ShouldReturnValidationException() throws Exception {
        //given
        authorRepository.deleteAll();
        assertThat(authorRepository.count()).isZero();

        Author author = new Author(null, null, null, new ArrayList<>());


        //when
        mockMvc.perform(post("/api/authors")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(status().isOk());

        //then
        assertThat(authorRepository.count()).isEqualTo(0);
//        Assert.assertThrows(Exception.class, () -> {
//
//        });


    }

    @Sql("/sql/create_author.sql")
    @Test
    @DisplayName(value = "Get Author")
    public void getAuthor_ShouldReturnAuthor() throws Exception {
        //given


        //when
        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.biography").value("smth"));


    }

    @Sql("/sql/create_author.sql")
    @Test
    @DisplayName(value = "Update Author")
    public void updateAuthor_ShouldReturnAuthor() throws Exception {
        //given


        Author newAuthor = new Author(null, "Denis", "new", new ArrayList<>());


        //when
        mockMvc.perform(put("/api/authors/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newAuthor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(newAuthor.getName()))
                .andExpect(jsonPath("$.biography").value(newAuthor.getBiography()));
        //then
        assertThat(authorRepository.count()).isEqualTo(1);

        Author createdAuthor = authorRepository.findAll().get(0);
        assertThat(createdAuthor.getName()).isEqualTo(newAuthor.getName());
        assertThat(createdAuthor.getBiography()).isEqualTo(newAuthor.getBiography());

    }


    @Test
    @DisplayName(value = "Update Author")
    public void updateAuthor_ShouldReturnNotFound() throws Exception {
        //given


        Author newAuthor = new Author(null, "Denis", "new", new ArrayList<>());


        //when
        mockMvc.perform(put("/api/authors/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newAuthor)))
                .andExpect(status().isNotFound());


    }


    @Sql("/sql/create_author.sql")
    @Test
    @DisplayName(value = "delete Author")
    public void deleteAuthor_ShouldReturnOK() throws Exception {
        //given


        //when
        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isOk());

        //then
        assertThat(authorRepository.count()).isZero();


    }


    @Test
    @DisplayName(value = "delete not founded Author")
    public void deleteAuthor_ShouldReturnException() throws Exception {
        //given


        //when
        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNotFound());

        //then
        assertThat(authorRepository.count()).isZero();


    }


}
