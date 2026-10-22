package br.com.anajulia.clubedolivro.service;

import br.com.anajulia.clubedolivro.dto.BookRequest;
import br.com.anajulia.clubedolivro.dto.BookResponse;
import br.com.anajulia.clubedolivro.entity.Book;
import br.com.anajulia.clubedolivro.exception.BusinessRuleException;
import br.com.anajulia.clubedolivro.exception.ResourceNotFoundException;
import br.com.anajulia.clubedolivro.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    // Injeção de dependência pelo construtor: o Spring cria o repository e entrega aqui
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> list() {
        return bookRepository.findAllByOrderByMeetingDateDesc()
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        return BookResponse.from(getBookOrThrow(id));
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        String title = request.title().trim();
        String author = request.author().trim();
        if (bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCase(title, author)) {
            throw new BusinessRuleException("O livro \"" + title + "\" de " + author + " já está cadastrado");
        }
        Book book = new Book(title, author, request.publicationYear(), request.meetingDate(), request.authorPresent());
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = getBookOrThrow(id);
        // Sem save(): dentro da transação o JPA percebe a mudança e faz o UPDATE no commit
        book.update(request.title().trim(), request.author().trim(),
                request.publicationYear(), request.meetingDate(), request.authorPresent());
        return BookResponse.from(book);
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