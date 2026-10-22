package br.com.anajulia.clubedolivro.service;

import br.com.anajulia.clubedolivro.dto.ReviewRequest;
import br.com.anajulia.clubedolivro.dto.ReviewResponse;
import br.com.anajulia.clubedolivro.entity.Book;
import br.com.anajulia.clubedolivro.entity.ReadingStatus;
import br.com.anajulia.clubedolivro.entity.Review;
import br.com.anajulia.clubedolivro.exception.BusinessRuleException;
import br.com.anajulia.clubedolivro.exception.ResourceNotFoundException;
import br.com.anajulia.clubedolivro.repository.BookRepository;
import br.com.anajulia.clubedolivro.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Service
public class ReviewService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final Clock clock;

    public ReviewService(BookRepository bookRepository, ReviewRepository reviewRepository, Clock clock) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public ReviewResponse findByBookId(Long bookId) {
        return reviewRepository.findById(bookId)
                .map(ReviewResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("O livro " + bookId + " ainda não tem avaliação"));
    }

    @Transactional
    public ReviewResponse save(Long bookId, ReviewRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Livro " + bookId + " não encontrado"));

        boolean attended = Boolean.TRUE.equals(request.attendedMeeting());
        String comment = (request.comment() == null || request.comment().isBlank()) ? null : request.comment().trim();

        if (request.readingStatus() == ReadingStatus.NOT_READ && (request.rating() != null || comment != null)) {
            throw new BusinessRuleException("Nota e comentário só valem para livros que você leu, mesmo sem terminar");
        }

        if (attended && book.getMeetingDate().isAfter(LocalDate.now(clock))) {
            throw new BusinessRuleException("O encontro de " + book.getMeetingDate() + " ainda não aconteceu");
        }

        Review review = reviewRepository.findById(bookId).orElseGet(() -> new Review(book));
        review.update(request.readingStatus(), attended, request.rating(), comment);
        return ReviewResponse.from(reviewRepository.saveAndFlush(review));
    }
}