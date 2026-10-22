package br.com.anajulia.clubedolivro.controller;

import br.com.anajulia.clubedolivro.dto.BookRequest;
import br.com.anajulia.clubedolivro.dto.BookResponse;
import br.com.anajulia.clubedolivro.exception.BusinessRuleException;
import br.com.anajulia.clubedolivro.exception.ResourceNotFoundException;
import br.com.anajulia.clubedolivro.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    private static final String AS_MENINAS_JSON = """
            {"title": "As Meninas", "author": "Lygia Fagundes Telles",
             "publicationYear": 1973, "meetingDate": "2026-02-22", "authorPresent": false}
            """;

    @Test
    void createReturns201WithLocation() throws Exception {
        when(bookService.create(any(BookRequest.class))).thenReturn(new BookResponse(
                1L, "As Meninas", "Lygia Fagundes Telles", 1973, LocalDate.of(2026, 2, 22), false, null));

        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(AS_MENINAS_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/books/1"))
                .andExpect(jsonPath("$.title").value("As Meninas"));
    }

    @Test
    void invalidFieldsReturn400WithErrors() throws Exception {
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "", "author": "Clarice Lispector", "publicationYear": 50}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("O título é obrigatório"))
                .andExpect(jsonPath("$.errors.publicationYear").value("Ano de publicação inválido"))
                .andExpect(jsonPath("$.errors.meetingDate").value("A data do encontro é obrigatória"));
    }

    @Test
    void duplicateBookReturns409() throws Exception {
        when(bookService.create(any(BookRequest.class)))
                .thenThrow(new BusinessRuleException("O livro já está cadastrado"));

        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(AS_MENINAS_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("O livro já está cadastrado"));
    }

    @Test
    void missingBookReturns404() throws Exception {
        when(bookService.findById(99L)).thenThrow(new ResourceNotFoundException("Livro 99 não encontrado"));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"));
    }
}