package com.aldren.service;

import com.aldren.entity.Book;
import com.aldren.entity.Borrowed;
import com.aldren.model.BookResponse;
import com.aldren.model.BorrowedRequest;
import com.aldren.model.BorrowedResponse;
import com.aldren.properties.BookProperties;
import com.aldren.repository.BookRepository;
import com.aldren.repository.BorrowedRepository;
import com.aldren.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableConfigurationProperties({BookProperties.class})
public class BorrowedService {

    private BorrowedRepository borrowedRepository;
    private BookRepository bookRepository;
    private BookProperties bookProperties;

    public BorrowedService(@Autowired BorrowedRepository borrowedRepository,
                           @Autowired BookRepository bookRepository,
                           @Autowired BookProperties bookProperties) {
        this.borrowedRepository = borrowedRepository;
        this.bookRepository = bookRepository;
        this.bookProperties = bookProperties;
    }

    public BorrowedResponse borrowBook(BorrowedRequest borrowedRequest) {
        if(borrowedRequest.getBookIds().length > bookProperties.getMaximumBorrowedByUser()) {
            return buildBorrowedResponse(borrowedRequest.getUserId(), String.format("Users can only borrow up to a maximum of %d books", bookProperties.getMaximumBorrowedByUser()));
        }

        List<BookResponse> bookResponseList = Arrays.stream(borrowedRequest.getBookIds())
                .map(bookId -> processBorrowedBook(bookId, borrowedRequest.getUserId()))
                .collect(Collectors.toList());

        return buildBorrowedResponse(borrowedRequest.getUserId(), null, bookResponseList);
    }

    private BorrowedResponse buildBorrowedResponse(String userId, String message) {
        return buildBorrowedResponse(userId, message, new ArrayList<>());
    }

    private BorrowedResponse buildBorrowedResponse(String userId, String message, List<BookResponse> bookResponseList) {
        return BorrowedResponse.builder()
                .userId(userId)
                .message(message)
                .bookResponseList(bookResponseList)
                .build();
    }

    private void setBookToBorrowed(Book book, String userId, String borrowedDate, String expiryDate) {
        book.setStatus(AppConstants.BOOK_STATUS_BORROWED);
        bookRepository.save(book);

        Borrowed borrowed = new Borrowed();
        borrowed.setId(UUID.randomUUID().toString());
        borrowed.setUserId(userId);
        borrowed.setBookId(book.getId());
        borrowed.setBorrowedDate(borrowedDate);
        borrowed.setExpiryDate(expiryDate);

        borrowedRepository.save(borrowed);
    }

    private BookResponse processBorrowedBook(String bookId, String userId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if(optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if(book.getStatus().equals(AppConstants.BOOK_STATUS_BORROWED)) {
                return BookResponse.builder()
                        .bookId(book.getId())
                        .bookName(book.getName())
                        .remarks("Book is currently unavailable.")
                        .build();
            }

            Date currentDate = new Date();
            LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            String borrowedDate = localDateTime.toString();
            String expiryDate = localDateTime.plusDays(bookProperties.getBorrowDuration()).toString();

            setBookToBorrowed(optionalBook.get(), userId, borrowedDate, expiryDate);

            return BookResponse.builder()
                    .bookId(book.getId())
                    .bookName(book.getName())
                    .toBeReturnedDate(expiryDate)
                    .build();
        }

        return BookResponse.builder()
                .bookId(bookId)
                .remarks("No book found")
                .build();
    }

}
