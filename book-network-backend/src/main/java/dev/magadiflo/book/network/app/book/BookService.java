package dev.magadiflo.book.network.app.book;

import dev.magadiflo.book.network.app.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Long save(BookRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = this.bookMapper.toBook(request);
        book.setOwner(user);
        return this.bookRepository.save(book).getId();
    }

    public BookResponse findById(Long bookId) {
        return this.bookRepository.findById(bookId)
                .map(this.bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No se encuentra el libro con el id " + bookId));
    }
}
