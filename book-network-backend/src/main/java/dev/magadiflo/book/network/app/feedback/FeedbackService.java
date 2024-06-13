package dev.magadiflo.book.network.app.feedback;

import dev.magadiflo.book.network.app.book.Book;
import dev.magadiflo.book.network.app.book.BookRepository;
import dev.magadiflo.book.network.app.exception.OperationNotPermittedException;
import dev.magadiflo.book.network.app.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeebackRepository feebackRepository;
    private final FeedbackMapper feedbackMapper;

    public Long save(FeedbackRequest request, Authentication authentication) {
        Book book = this.bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el libro con id " + request.bookId()));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("No puedes dar comentarios sobre un libro archivado o que no se puede compartir");
        }

        User user = (User) authentication.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("No puedes dar comentarios sobre tu propio libro");
        }

        Feedback feedback = this.feedbackMapper.toFeedback(request);
        return this.feebackRepository.save(feedback).getId();
    }
}
