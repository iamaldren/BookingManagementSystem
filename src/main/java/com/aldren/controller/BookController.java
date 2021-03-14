package com.aldren.controller;

import com.aldren.entity.Book;
import com.aldren.exception.RecordNotFoundException;
import com.aldren.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        List<Book> books = new ArrayList<>();

        if(isbn.isPresent()) {

        }

        if(name.isPresent()) {

        }

        books = bookService.getBooks().orElseThrow(() -> new RecordNotFoundException("No book records exist"));

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(books);
    }

}
