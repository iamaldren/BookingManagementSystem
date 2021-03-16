package com.aldren.service;

import com.aldren.entity.Book;
import com.aldren.exception.BadRequestException;
import com.aldren.exception.RecordNotFoundException;
import com.aldren.repository.BookRepository;
import com.aldren.util.AppConstants;
import com.aldren.util.BookUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class BookService {

    private BookRepository bookRepository;

    public BookService(@Autowired BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<List<Book>> getBookBy(String queryName, String value) {
        log.info(String.format("Getting book list by %s.", queryName));

        switch (queryName) {
            case AppConstants.QUERY_BY_ISBN:
                return returnOptionalBook(bookRepository.findByIsbn(value));
            case AppConstants.QUERY_BY_NAME:
                return returnOptionalBook(bookRepository.findByName(value));
            default:
                return returnOptionalBook((List<Book>) bookRepository.findAll());
        }
    }

    private Optional<List<Book>> returnOptionalBook(List<Book> books) {
        if(books.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(books);
    }

    public void saveBook(Book book) {
        book.setId(UUID.randomUUID().toString());

        log.info(String.format("Saving book %1$s with ID of %2$s.", book.getName(), book.getId()));

        if(book.getIsbn() == null) {
            log.warn(String.format("No ISBN entered for %s. Randomly generating one for the entry.", book.getName()));
            book.setIsbn(BookUtil.makeISBN());
        }

        book.setStatus(AppConstants.BOOK_STATUS_AVAILABLE);
        bookRepository.save(book);
    }

    public void updateBook(Book book) throws RecordNotFoundException, BadRequestException {
        if (book.getId() == null) {
            throw new BadRequestException("Book ID can not be null or empty when updating an entry");
        }

        log.info(String.format("Updating book with ID of %s.", book.getId()));

        bookRepository.findById(book.getId()).orElseThrow(() -> new RecordNotFoundException(String.format("Record with ID of %s does not exists.", book.getId())));
        bookRepository.save(book);
    }

    public void deleteBook(String id) throws RecordNotFoundException {
        log.info(String.format("Deleting book with ID of %s.", id));
        bookRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(String.format("Record with ID of %s does not exists.", id)));
        bookRepository.deleteById(id);
    }

}
