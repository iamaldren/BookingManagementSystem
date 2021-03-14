package com.aldren.service;

import com.aldren.entity.Book;
import com.aldren.repository.BookRepository;
import com.aldren.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private BookRepository bookRepository;

    public BookService(@Autowired BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<List<Book>> getBookBy(String queryName, String value) {
        switch (queryName) {
            case AppConstants.QUERY_BY_ISBN:
                return Optional.ofNullable(bookRepository.findByIsbn(value));
            case AppConstants.QUERY_BY_NAME:
                return Optional.ofNullable(bookRepository.findByName(value));
            default:
                return Optional.empty();
        }
    }

    public Optional<List<Book>> getBooks() {
        return Optional.ofNullable((List<Book>) bookRepository.findAll());
    }

}
