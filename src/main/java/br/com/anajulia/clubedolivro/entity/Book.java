package br.com.anajulia.clubedolivro.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "meeting_date", nullable = false)
    private LocalDate meetingDate;

    @Column(name = "author_present", nullable = false)
    private boolean authorPresent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Book() {
        // exigido pelo JPA
    }

    public Book(String title, String author, Integer publicationYear, LocalDate meetingDate, boolean authorPresent) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.meetingDate = meetingDate;
        this.authorPresent = authorPresent;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public void update(String title, String author, Integer publicationYear, LocalDate meetingDate, boolean authorPresent) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.meetingDate = meetingDate;
        this.authorPresent = authorPresent;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Integer getPublicationYear() { return publicationYear; }
    public LocalDate getMeetingDate() { return meetingDate; }
    public boolean isAuthorPresent() { return authorPresent; }
    public Instant getCreatedAt() { return createdAt; }
}