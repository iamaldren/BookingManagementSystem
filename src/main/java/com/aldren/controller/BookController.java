package com.aldren.controller;

import com.aldren.entity.Book;
import com.aldren.exception.RecordNotFoundException;
import com.aldren.service.BookService;
import com.aldren.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class BookController {

    private BookService bookService;

    public BookController(@Autowired BookService bookService) {
        this.bookService = bookService;
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

}
