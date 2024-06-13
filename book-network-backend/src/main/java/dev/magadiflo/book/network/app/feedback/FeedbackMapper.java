package dev.magadiflo.book.network.app.feedback;

import dev.magadiflo.book.network.app.book.Book;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {

    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder().id(request.bookId()).build())
                .build();
    }
}
