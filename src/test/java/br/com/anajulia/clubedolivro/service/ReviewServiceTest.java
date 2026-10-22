package br.com.anajulia.clubedolivro.service;

import br.com.anajulia.clubedolivro.dto.ReviewRequest;
import br.com.anajulia.clubedolivro.dto.ReviewResponse;
import br.com.anajulia.clubedolivro.entity.Book;
import br.com.anajulia.clubedolivro.entity.ReadingStatus;
import br.com.anajulia.clubedolivro.entity.Review;
import br.com.anajulia.clubedolivro.exception.BusinessRuleException;
import br.com.anajulia.clubedolivro.repository.BookRepository;
import br.com.anajulia.clubedolivro.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    private ReviewService reviewService;

    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-10-22T15:00:00Z"), ZoneId.of("America/Sao_Paulo"));

    private final Book asMeninas = new Book("As Meninas", "Lygia Fagundes Telles", 1973, LocalDate.of(2026, 2, 22), false);
    private final Book jardimSecreto = new Book("O Jardim Secreto", "Frances Hodgson Burnett", 1911, LocalDate.of(2026, 10, 25), false);

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(bookRepository, reviewRepository, fixedClock);
    }

    @Test
    void readButMissedTheMeetingCanBeRated() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(asMeninas));
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());
        when(reviewRepository.saveAndFlush(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse response = reviewService.save(1L, new ReviewRequest(ReadingStatus.READ, false, 5, "  Amei  "));

        assertThat(response.rating()).isEqualTo(5);
        assertThat(response.comment()).isEqualTo("Amei");
        assertThat(response.attendedMeeting()).isFalse();
    }

    @Test
    void notReadCannotHaveRating() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(asMeninas));

        assertThatThrownBy(() -> reviewService.save(1L, new ReviewRequest(ReadingStatus.NOT_READ, true, 4, null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("só valem para livros que você leu");
        verify(reviewRepository, never()).saveAndFlush(any());
    }

    @Test
    void cannotAttendAFutureMeeting() {
        when(bookRepository.findById(2L)).thenReturn(Optional.of(jardimSecreto));

        assertThatThrownBy(() -> reviewService.save(2L, new ReviewRequest(ReadingStatus.READ, true, null, null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ainda não aconteceu");
    }
}