package br.com.anajulia.clubedolivro.service;

import br.com.anajulia.clubedolivro.dto.BookRequest;
import br.com.anajulia.clubedolivro.dto.BookResponse;
import br.com.anajulia.clubedolivro.entity.Book;
import br.com.anajulia.clubedolivro.exception.BusinessRuleException;
import br.com.anajulia.clubedolivro.exception.ResourceNotFoundException;
import br.com.anajulia.clubedolivro.repository.BookRepository;
import br.com.anajulia.clubedolivro.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private BookService bookService;

    private final BookRequest asMeninas = new BookRequest(
            "  As Meninas ", "Lygia Fagundes Telles", 1973, LocalDate.of(2026, 2, 22), false);

    @Test
    void createSavesTrimmedBook() {
        when(bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCase("As Meninas", "Lygia Fagundes Telles"))
                .thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse response = bookService.create(asMeninas);

        assertThat(response.title()).isEqualTo("As Meninas");
        assertThat(response.meetingDate()).isEqualTo(LocalDate.of(2026, 2, 22));
    }

    @Test
    void createRejectsDuplicateBook() {
        when(bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCase("As Meninas", "Lygia Fagundes Telles"))
                .thenReturn(true);

        assertThatThrownBy(() -> bookService.create(asMeninas))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("já está cadastrado");
        verify(bookRepository, never()).save(any());
    }

    @Test
    void findByIdThrowsWhenBookDoesNotExist() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Livro 99 não encontrado");
    }
}