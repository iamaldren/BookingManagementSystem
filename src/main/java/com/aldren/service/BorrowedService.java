package com.aldren.service;

import com.aldren.entity.Book;
import com.aldren.entity.Borrowed;
import com.aldren.entity.User;
import com.aldren.enums.Operation;
import com.aldren.model.BookResponse;
import com.aldren.model.BorrowedRequest;
import com.aldren.model.BorrowedResponse;
import com.aldren.properties.BookProperties;
import com.aldren.repository.BookRepository;
import com.aldren.repository.BorrowedRepository;
import com.aldren.repository.UserRepository;
import com.aldren.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableConfigurationProperties({BookProperties.class})
public class BorrowedService {

    private BorrowedRepository borrowedRepository;
    private BookRepository bookRepository;
    private BookProperties bookProperties;
    private UserRepository userRepository;

    private ReentrantLock mutex = new ReentrantLock();

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
        log.info(String.format("Borrowing book for user %s", borrowedRequest.getUserId()));

        if(!isUserValid(borrowedRequest.getUserId())) {
            String errorMessage = String.format("User %s doesn't exists.", borrowedRequest.getUserId());
            log.warn(errorMessage);
            return buildBorrowedResponse(borrowedRequest.getUserId(), errorMessage);
        }

        if(borrowedRequest.getBookIds().length > bookProperties.getMaximumBorrowedByUser()) {
            return buildBorrowedResponse(borrowedRequest.getUserId(), String.format("Users can only borrow up to a maximum of %d books", bookProperties.getMaximumBorrowedByUser()));
        }

        String errorMessage = canUserStillBorrow(borrowedRequest.getUserId(), borrowedRequest.getBookIds().length);
        if(!errorMessage.isEmpty()) {
            return buildBorrowedResponse(borrowedRequest.getUserId(), errorMessage);
        }

        try {
            mutex.lock();

            List<String> borrowedBookIds = new ArrayList<>();
            LocalDateTime borrowedDate = getCurrentLocalDateTime();
            String expiryDate = borrowedDate.plusDays(bookProperties.getBorrowDuration()).toString();

            List<BookResponse> bookResponseList = Arrays.stream(borrowedRequest.getBookIds())
                    .map(bookId -> buildBorrowedBookResponse(bookId, expiryDate, borrowedBookIds, Operation.BORROW))
                    .collect(Collectors.toList());

            setBookToBorrowed(borrowedBookIds, borrowedRequest.getUserId(), borrowedDate.toString(), expiryDate);

            return buildBorrowedResponse(borrowedRequest.getUserId(), null, bookResponseList);
        } finally {
            mutex.unlock();
        }
    }

    public BorrowedResponse returnBook(BorrowedRequest borrowedRequest) {
        log.info(String.format("Returning book for user %s", borrowedRequest.getUserId()));

        if(!isUserValid(borrowedRequest.getUserId())) {
            String errorMessage = String.format("User %s doesn't exists.", borrowedRequest.getUserId());
            log.warn(errorMessage);
            return buildBorrowedResponse(borrowedRequest.getUserId(), errorMessage);
        }

        List<String> returnedBookIds = new ArrayList<>();
        Date currentDate = new Date();
        LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        List<BookResponse> bookResponseList = Arrays.stream(borrowedRequest.getBookIds())
                .map(bookId -> buildBorrowedBookResponse(bookId, localDateTime.toString(), returnedBookIds, Operation.RETURN))
                .collect(Collectors.toList());

        setBookToAvailable(returnedBookIds, borrowedRequest.getUserId(), localDateTime.toString());

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
            borrowed.setReturnedDate("");

            borrowedList.add(borrowed);
        });

        bookRepository.saveAll(bookList);
        borrowedRepository.saveAll(borrowedList);
    }

    private void setBookToAvailable(List<String> returnedBookIds, String userId, String returnedDate) {
        List<Book> bookList = (List<Book>) bookRepository.findAllById(returnedBookIds);
        List<Borrowed> borrowedList = borrowedRepository.findByUserIdAndReturnedDate(userId, "");

        bookList.forEach(book -> book.setStatus(AppConstants.BOOK_STATUS_AVAILABLE));
        borrowedList.forEach(borrowed -> {
            borrowed.setReturnedDate(returnedDate);
        });

        bookRepository.saveAll(bookList);
        borrowedRepository.saveAll(borrowedList);
    }

    private BookResponse buildBorrowedBookResponse(String bookId, String actionDate, List<String> bookIds, Operation operation) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if(optionalBook.isPresent()) {
            Book book = optionalBook.get();
            BookResponse.BookResponseBuilder bookResponseBuilder = BookResponse.builder()
                    .bookId(book.getId())
                    .bookName(book.getName());

            switch(operation) {
                case BORROW:
                    if(book.getStatus().equals(AppConstants.BOOK_STATUS_BORROWED)) {
                        log.warn(String.format("Book with ID of %s is lend out.", book.getId()));
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

            bookIds.add(book.getId());
            return bookResponseBuilder.build();
        }

        log.warn(String.format("Book with ID of %s is not found.", bookId));
        return BookResponse.builder()
                .bookId(bookId)
                .remarks("No book found")
                .build();
    }

    private boolean isUserValid(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.isPresent();
    }

    private LocalDateTime getCurrentLocalDateTime() {
        Date currentDate = new Date();
        return currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private String canUserStillBorrow(String userId, int numOfBooksBorrowed) {
        List<Borrowed> borrowedList = borrowedRepository.findByUserIdAndReturnedDate(userId, "");

        int numOfBooksThatCanBorrow = bookProperties.getMaximumBorrowedByUser() - borrowedList.size();

        if(numOfBooksBorrowed > numOfBooksThatCanBorrow) {
            String message = String.format("User %s has %1$d books borrowed, a user can only borrow up to maximum of %2$d books.", userId, borrowedList.size(), bookProperties.getMaximumBorrowedByUser());
            log.warn(message);
            return message;
        }

        return "";
    }

}
