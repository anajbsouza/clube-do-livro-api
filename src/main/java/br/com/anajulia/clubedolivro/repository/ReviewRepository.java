package br.com.anajulia.clubedolivro.repository;

import br.com.anajulia.clubedolivro.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}