package br.com.anajulia.clubedolivro.service;

import br.com.anajulia.clubedolivro.dto.BookRequest;
import br.com.anajulia.clubedolivro.dto.BookResponse;
import br.com.anajulia.clubedolivro.entity.Book;
import br.com.anajulia.clubedolivro.entity.Review;
import br.com.anajulia.clubedolivro.exception.BusinessRuleException;
import br.com.anajulia.clubedolivro.exception.ResourceNotFoundException;
import br.com.anajulia.clubedolivro.repository.BookRepository;
import br.com.anajulia.clubedolivro.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public BookService(BookRepository bookRepository, ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> list() {
        Map<Long, Review> reviewsByBookId = reviewRepository.findAll().stream()
                .collect(Collectors.toMap(Review::getBookId, Function.identity()));

        return bookRepository.findAllByOrderByMeetingDateDesc().stream()
                .map(book -> BookResponse.from(book, reviewsByBookId.get(book.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        Book book = getBookOrThrow(id);
        return BookResponse.from(book, reviewRepository.findById(id).orElse(null));
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        String title = request.title().trim();
        String author = request.author().trim();
        if (bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCase(title, author)) {
            throw new BusinessRuleException("O livro \"" + title + "\" de " + author + " já está cadastrado");
        }
        Book book = new Book(title, author, request.publicationYear(), request.meetingDate(),
                Boolean.TRUE.equals(request.authorPresent()));
        return BookResponse.from(bookRepository.save(book), null);
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = getBookOrThrow(id);
        book.update(request.title().trim(), request.author().trim(), request.publicationYear(),
                request.meetingDate(), Boolean.TRUE.equals(request.authorPresent()));
        return BookResponse.from(book, reviewRepository.findById(id).orElse(null));
    }

    @Transactional
    public void delete(Long id) {
        bookRepository.delete(getBookOrThrow(id));
    }

    private Book getBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro " + id + " não encontrado"));
    }
}