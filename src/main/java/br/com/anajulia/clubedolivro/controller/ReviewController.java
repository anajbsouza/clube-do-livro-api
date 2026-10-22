package br.com.anajulia.clubedolivro.controller;

import br.com.anajulia.clubedolivro.dto.ReviewRequest;
import br.com.anajulia.clubedolivro.dto.ReviewResponse;
import br.com.anajulia.clubedolivro.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books/{bookId}/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ReviewResponse find(@PathVariable Long bookId) {
        return reviewService.findByBookId(bookId);
    }

    @PutMapping
    public ReviewResponse save(@PathVariable Long bookId, @Valid @RequestBody ReviewRequest request) {
        return reviewService.save(bookId, request);
    }
}