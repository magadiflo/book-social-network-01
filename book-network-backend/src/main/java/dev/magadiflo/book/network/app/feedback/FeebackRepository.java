package dev.magadiflo.book.network.app.feedback;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeebackRepository extends JpaRepository<Feedback, Long> {
}
