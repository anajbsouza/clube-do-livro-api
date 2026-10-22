package br.com.anajulia.clubedolivro.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "review")
public class Review {

    // A chave da review é o id do livro (@MapsId): uma avaliação por livro
    @Id
    private Long bookId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "reading_status", nullable = false)
    private ReadingStatus readingStatus;

    @Column(name = "attended_meeting", nullable = false)
    private boolean attendedMeeting;

    private Integer rating;

    private String comment;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Review() {
    }

    public Review(Book book) {
        this.book = book;
    }

    @PrePersist
    @PreUpdate
    void touch() {
        this.updatedAt = Instant.now();
    }

    public void update(ReadingStatus readingStatus, boolean attendedMeeting, Integer rating, String comment) {
        this.readingStatus = readingStatus;
        this.attendedMeeting = attendedMeeting;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getBookId() { return bookId; }
    public Book getBook() { return book; }
    public ReadingStatus getReadingStatus() { return readingStatus; }
    public boolean isAttendedMeeting() { return attendedMeeting; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public Instant getUpdatedAt() { return updatedAt; }
}