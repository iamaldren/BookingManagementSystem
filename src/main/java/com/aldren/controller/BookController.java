package com.aldren.controller;

import com.aldren.entity.Book;
import com.aldren.enums.Operation;
import com.aldren.exception.BadRequestException;
import com.aldren.exception.DefaultInternalServerException;
import com.aldren.exception.RecordNotFoundException;
import com.aldren.model.BorrowedRequest;
import com.aldren.model.BorrowedResponse;
import com.aldren.service.BookService;
import com.aldren.service.BorrowedService;
import com.aldren.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class BookController {

    private BookService bookService;
    private BorrowedService borrowedService;

    public BookController(@Autowired BookService bookService,
                          @Autowired BorrowedService borrowedService) {
        this.bookService = bookService;
        this.borrowedService = borrowedService;
    }

    @GetMapping(value = "/books")
    public ResponseEntity getBooks(@RequestParam Optional<String> name, @RequestParam Optional<String> isbn) throws RecordNotFoundException, DefaultInternalServerException {
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

        List<Book> books;
        try {
            books = bookService
                    .getBookBy(query, value)
                    .orElseThrow(() -> new RecordNotFoundException("No book records exist"));
        } catch (RedisConnectionFailureException e) {
            log.error("Error retrieving book list.", e);
            throw new DefaultInternalServerException(e.getLocalizedMessage());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new DefaultInternalServerException(e.getLocalizedMessage());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(books);
    }

    @PostMapping(value = "/books")
    public void saveBook(@RequestBody Book book) throws DefaultInternalServerException {
        try {
            bookService.saveBook(book);
        } catch (RedisConnectionFailureException e) {
            log.error("Error retrieving book list.", e);
            throw new DefaultInternalServerException(e.getLocalizedMessage());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new DefaultInternalServerException(e.getLocalizedMessage());
        }
    }

    @PutMapping(value = "/books")
    public void updateBook(@RequestBody Book book) throws DefaultInternalServerException, RecordNotFoundException, BadRequestException {
        try {
            bookService.updateBook(book);
        } catch (RecordNotFoundException | BadRequestException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (RedisConnectionFailureException e) {
            log.error("Error retrieving book list.", e);
            throw new DefaultInternalServerException(e.getLocalizedMessage());
        }  catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new DefaultInternalServerException(e.getLocalizedMessage());
        }
    }

    @DeleteMapping(value = "/books/{id}")
    public void deleteBook(@PathVariable String id) throws RecordNotFoundException, DefaultInternalServerException {
        try {
            bookService.deleteBook(id);
        } catch (RecordNotFoundException e) {
            log.warn(e.getLocalizedMessage());
            throw e;
        } catch (RedisConnectionFailureException e) {
            log.error("Error retrieving book list.", e);
            throw new DefaultInternalServerException(e.getLocalizedMessage());
        }   catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new DefaultInternalServerException(e.getLocalizedMessage());
        }
    }

    @PostMapping(value = "/books/operations")
    public BorrowedResponse bookOperations(@RequestBody BorrowedRequest borrowedRequest) throws DefaultInternalServerException, BadRequestException {
        try {
            Operation operation = Operation.get(borrowedRequest.getOperation());

            switch (operation) {
                case BORROW:
                    return borrowedService.borrowBook(borrowedRequest);
                case RETURN:
                    return borrowedService.returnBook(borrowedRequest);
                default:
                    throw new BadRequestException(String.format("The operation \"%s\" is not supported.", borrowedRequest.getOperation()));
            }
        } catch (BadRequestException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (RedisConnectionFailureException e) {
            log.error("Error returning book/s.", e);
            throw new DefaultInternalServerException(e.getLocalizedMessage());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new DefaultInternalServerException(e.getLocalizedMessage());
        }
    }

}
