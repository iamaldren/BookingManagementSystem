package com.aldren.controller;

import com.aldren.entity.Book;
import com.aldren.exception.RecordNotFoundException;
import com.aldren.model.BorrowedRequest;
import com.aldren.model.BorrowedResponse;
import com.aldren.service.BookService;
import com.aldren.service.BorrowedService;
import com.aldren.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class BookController {

    private BookService bookService;
    private BorrowedService borrowedService;

    public BookController(@Autowired BookService bookService,
                          @Autowired BorrowedService borrowedService) {
        this.bookService = bookService;
        this.borrowedService = borrowedService;
    }

    @GetMapping(value = "/books")
    public ResponseEntity getBooks(@RequestParam Optional<String> name, @RequestParam Optional<String> isbn) throws RecordNotFoundException {
        String query = AppConstants.QUERY_ALL;
        String value = "";

        if(isbn.isPresent()) {
            query = AppConstants.QUERY_BY_ISBN;
            value = isbn.get();
        }

        if(name.isPresent() && !query.equals(AppConstants.QUERY_BY_ISBN)) {
            query = AppConstants.QUERY_BY_NAME;
            value = name.get();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookService
                        .getBookBy(query, value)
                        .orElseThrow(() -> new RecordNotFoundException("No book records exist")));
    }

    @PostMapping(value = "/books")
    public void saveBook(@RequestBody Book book) {
        bookService.saveBook(book);
    }

    @PutMapping(value = "/books")
    public void updateBook(@RequestBody Book book) throws RecordNotFoundException {
        bookService.updateBook(book);
    }

    @DeleteMapping(value = "/books/{id}")
    public void deleteBook(@PathVariable String id) throws RecordNotFoundException {
        bookService.deleteBook(id);
    }

    @PostMapping(value = "/books/borrow")
    public BorrowedResponse borrowBook(@RequestBody BorrowedRequest borrowedRequest) {
        return borrowedService.borrowBook(borrowedRequest);
    }

    @PostMapping(value = "/books/return")
    public BorrowedResponse returnBook(@RequestBody BorrowedRequest borrowedRequest) {
        return borrowedService.returnBook(borrowedRequest);
    }

}
