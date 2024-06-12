package dev.magadiflo.book.network.app.book;

import dev.magadiflo.book.network.app.common.PageResponse;
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

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(@RequestParam(defaultValue = "0", required = false) int page,
                                                                   @RequestParam(defaultValue = "10", required = false) int size,
                                                                   Authentication authentication) {
        return ResponseEntity.ok(this.bookService.findAllBooks(page, size, authentication));
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(@RequestParam(defaultValue = "0", required = false) int page,
                                                                          @RequestParam(defaultValue = "10", required = false) int size,
                                                                          Authentication authentication) {
        return ResponseEntity.ok(this.bookService.findAllBooksByOwner(page, size, authentication));
    }

    @GetMapping(path = "/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(@RequestParam(defaultValue = "0", required = false) int page,
                                                                                   @RequestParam(defaultValue = "10", required = false) int size,
                                                                                   Authentication authentication) {
        return ResponseEntity.ok(this.bookService.findAllBorrowedBooks(page, size, authentication));
    }

    @GetMapping(path = "/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(@RequestParam(defaultValue = "0", required = false) int page,
                                                                                   @RequestParam(defaultValue = "10", required = false) int size,
                                                                                   Authentication authentication) {
        return ResponseEntity.ok(this.bookService.findAllReturnedBooks(page, size, authentication));
    }


    @GetMapping(path = "/{bookId}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable Long bookId) {
        return ResponseEntity.ok(this.bookService.findById(bookId));
    }

    @PostMapping
    public ResponseEntity<Long> saveBook(@Valid @RequestBody BookRequest request, Authentication authentication) {
        return new ResponseEntity<>(this.bookService.save(request, authentication), HttpStatus.CREATED);
    }

    @PatchMapping(path = "/shareable/{bookId}")
    public ResponseEntity<Long> updateShareableStatus(@PathVariable Long bookId, Authentication authentication) {
        return ResponseEntity.ok(this.bookService.updateShareableStatus(bookId, authentication));
    }
}
