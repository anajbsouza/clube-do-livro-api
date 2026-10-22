package br.com.anajulia.clubedolivro.dto;

import br.com.anajulia.clubedolivro.entity.Book;
import br.com.anajulia.clubedolivro.entity.Review;

import java.time.LocalDate;

public record BookResponse(
        Long id,
        String title,
        String author,
        Integer publicationYear,
        LocalDate meetingDate,
        boolean authorPresent,
        ReviewResponse review
) {
    public static BookResponse from(Book book, Review review) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublicationYear(),
                book.getMeetingDate(),
                book.isAuthorPresent(),
                review == null ? null : ReviewResponse.from(review)
        );
    }
}