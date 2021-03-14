package com.aldren.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Book {

    private String id;
    private String isbn;
    private String name;
    private String author;
    private String publishDate;
    private String summary;


}
