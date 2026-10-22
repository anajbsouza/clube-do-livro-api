package br.com.anajulia.clubedolivro.dto;

import br.com.anajulia.clubedolivro.entity.ReadingStatus;
import br.com.anajulia.clubedolivro.entity.Review;

import java.time.Instant;

public record ReviewResponse(
        ReadingStatus readingStatus,
        boolean attendedMeeting,
        Integer rating,
        String comment,
        Instant updatedAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getReadingStatus(),
                review.isAttendedMeeting(),
                review.getRating(),
                review.getComment(),
                review.getUpdatedAt()
        );
    }
}