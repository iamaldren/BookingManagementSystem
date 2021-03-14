package com.aldren.repository;

import com.aldren.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book, String> {

    List<Book> findByName(String name);

    List<Book> findByIsbn(String isbn);

}
