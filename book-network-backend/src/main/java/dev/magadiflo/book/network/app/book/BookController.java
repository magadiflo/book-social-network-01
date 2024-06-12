package dev.magadiflo.book.network.app.book;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Book", description = "API de Book")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/books")
public class BookController {

    private final BookService bookService;

    @GetMapping(path = "/{bookId}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable Long bookId) {
        return ResponseEntity.ok(this.bookService.findById(bookId));
    }

    @PostMapping
    public ResponseEntity<Long> saveBook(@Valid @RequestBody BookRequest request, Authentication authentication) {
        return new ResponseEntity<>(this.bookService.save(request, authentication), HttpStatus.CREATED);
    }
}
