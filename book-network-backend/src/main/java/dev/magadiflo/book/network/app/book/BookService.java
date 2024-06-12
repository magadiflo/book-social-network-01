package dev.magadiflo.book.network.app.book;

import dev.magadiflo.book.network.app.common.PageResponse;
import dev.magadiflo.book.network.app.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> bookPage = this.bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = bookPage.stream()
                .map(this.bookMapper::toBookResponse)
                .toList();

        return PageResponse.<BookResponse>builder()
                .content(bookResponses)
                .number(bookPage.getNumber())
                .size(bookPage.getSize())
                .totalElements(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .first(bookPage.isFirst())
                .last(bookPage.isLast())
                .build();
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> bookPage = this.bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = bookPage.stream()
                .map(this.bookMapper::toBookResponse)
                .toList();

        return PageResponse.<BookResponse>builder()
                .content(bookResponses)
                .number(bookPage.getNumber())
                .size(bookPage.getSize())
                .totalElements(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .first(bookPage.isFirst())
                .last(bookPage.isLast())
                .build();
    }

    public BookResponse findById(Long bookId) {
        return this.bookRepository.findById(bookId)
                .map(this.bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No se encuentra el libro con el id " + bookId));
    }

    public Long save(BookRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = this.bookMapper.toBook(request);
        book.setOwner(user);
        return this.bookRepository.save(book).getId();
    }
}
