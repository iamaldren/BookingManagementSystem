package com.aldren.service;

import com.aldren.entity.Book;
import com.aldren.entity.Borrowed;
import com.aldren.entity.User;
import com.aldren.model.BookResponse;
import com.aldren.model.BorrowedRequest;
import com.aldren.model.BorrowedResponse;
import com.aldren.properties.BookProperties;
import com.aldren.repository.BookRepository;
import com.aldren.repository.BorrowedRepository;
import com.aldren.repository.UserRepository;
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
    private UserRepository userRepository;

    private static final String ACTION_BORROW = "borrow";
    private static final String ACTION_RETURN = "return";

    public BorrowedService(@Autowired BorrowedRepository borrowedRepository,
                           @Autowired BookRepository bookRepository,
                           @Autowired BookProperties bookProperties,
                           @Autowired UserRepository userRepository) {
        this.borrowedRepository = borrowedRepository;
        this.bookRepository = bookRepository;
        this.bookProperties = bookProperties;
        this.userRepository = userRepository;
    }

    public BorrowedResponse borrowBook(BorrowedRequest borrowedRequest) {
        if(!isUserValid(borrowedRequest.getUserId())) {
            return buildBorrowedResponse(borrowedRequest.getUserId(), String.format("User %s doesn't exists.", borrowedRequest.getUserId()));
        }

        if(borrowedRequest.getBookIds().length > bookProperties.getMaximumBorrowedByUser()) {
            return buildBorrowedResponse(borrowedRequest.getUserId(), String.format("Users can only borrow up to a maximum of %d books", bookProperties.getMaximumBorrowedByUser()));
        }

        List<String> borrowedBookIds = new ArrayList<>();
        Date currentDate = new Date();
        LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        String expiryDate = localDateTime.plusDays(bookProperties.getBorrowDuration()).toString();

        List<BookResponse> bookResponseList = Arrays.stream(borrowedRequest.getBookIds())
                .map(bookId -> buildBorrowedBookResponse(bookId, expiryDate, borrowedBookIds, ACTION_BORROW))
                .collect(Collectors.toList());

        setBookToBorrowed(borrowedBookIds, borrowedRequest.getUserId(), localDateTime.toString(), expiryDate);

        return buildBorrowedResponse(borrowedRequest.getUserId(), null, bookResponseList);
    }

    public BorrowedResponse returnBook(BorrowedRequest borrowedRequest) {
        if(!isUserValid(borrowedRequest.getUserId())) {
            return buildBorrowedResponse(borrowedRequest.getUserId(), String.format("User %s doesn't exists.", borrowedRequest.getUserId()));
        }

        List<String> returnedBookIds = new ArrayList<>();
        Date currentDate = new Date();
        LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        List<BookResponse> bookResponseList = Arrays.stream(borrowedRequest.getBookIds())
                .map(bookId -> buildBorrowedBookResponse(bookId, localDateTime.toString(), returnedBookIds, ACTION_RETURN))
                .collect(Collectors.toList());

        setBookToAvailable(returnedBookIds, localDateTime.toString());

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

    private void setBookToBorrowed(List<String> borrowedBookIds, String userId, String borrowedDate, String expiryDate) {
        List<Book> bookList = (List<Book>) bookRepository.findAllById(borrowedBookIds);

        List<Borrowed> borrowedList = new ArrayList<>();
        bookList.forEach(book -> {
            book.setStatus(AppConstants.BOOK_STATUS_BORROWED);

            Borrowed borrowed = new Borrowed();
            borrowed.setId(UUID.randomUUID().toString());
            borrowed.setUserId(userId);
            borrowed.setBookId(book.getId());
            borrowed.setBorrowedDate(borrowedDate);
            borrowed.setExpiryDate(expiryDate);

            borrowedList.add(borrowed);
        });

        bookRepository.saveAll(bookList);
        borrowedRepository.saveAll(borrowedList);
    }

    private void setBookToAvailable(List<String> returnedBookIds, String returnedDate) {
        List<Book> bookList = (List<Book>) bookRepository.findAllById(returnedBookIds);
        List<Borrowed> borrowedList = borrowedRepository.findByByBookIdIn(returnedBookIds);

        bookList.forEach(book -> book.setStatus(AppConstants.BOOK_STATUS_AVAILABLE));
        borrowedList.forEach(borrowed -> borrowed.setReturnedDate(returnedDate));

        bookRepository.saveAll(bookList);
        borrowedRepository.saveAll(borrowedList);
    }

    private BookResponse buildBorrowedBookResponse(String bookId, String actionDate, List<String> borrowedBookIds, String action) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if(optionalBook.isPresent()) {
            Book book = optionalBook.get();
            BookResponse.BookResponseBuilder bookResponseBuilder = BookResponse.builder()
                    .bookId(book.getId())
                    .bookName(book.getName());

            switch(action) {
                case ACTION_BORROW:
                    if(book.getStatus().equals(AppConstants.BOOK_STATUS_BORROWED)) {
                        return BookResponse.builder()
                                .bookId(book.getId())
                                .bookName(book.getName())
                                .remarks("Book is currently unavailable.")
                                .build();
                    }
                    bookResponseBuilder.toBeReturnedDate(actionDate);
                    break;
                default:
                    bookResponseBuilder.returnedDate(actionDate);
            }

            return bookResponseBuilder.build();
        }

        return BookResponse.builder()
                .bookId(bookId)
                .remarks("No book found")
                .build();
    }

    private boolean isUserValid(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.isPresent();
    }

}
