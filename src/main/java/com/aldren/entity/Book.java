package com.aldren.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class Book {

    private long id;
    private String isbn;
    private String name;
    private String author;
    private String publishDate;
    private String summary;


}
