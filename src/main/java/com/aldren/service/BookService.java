package com.aldren.service;

import com.aldren.entity.Book;
import com.aldren.exception.RecordNotFoundException;
import com.aldren.repository.BookRepository;
import com.aldren.util.AppConstants;
import com.aldren.util.BookUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {

    private BookRepository bookRepository;

    public BookService(@Autowired BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<List<Book>> getBookBy(String queryName, String value) {
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
        book.setIsbn(BookUtil.makeISBN());
        bookRepository.save(book);
    }

    public void updateBook(Book book) throws RecordNotFoundException {
        bookRepository.findById(book.getId()).orElseThrow(() -> new RecordNotFoundException(String.format("Record with ID of %s does not exists.", book.getId())));
        bookRepository.save(book);
    }

    public void deleteBook(String id) throws RecordNotFoundException {
        bookRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(String.format("Record with ID of %s does not exists.", id)));
        bookRepository.deleteById(id);
    }

}
