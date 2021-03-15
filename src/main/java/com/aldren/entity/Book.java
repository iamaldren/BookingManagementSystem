package com.aldren.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash("Book")
public class Book {

    private String id;
    private String isbn;
    private String name;
    private String author;
    private String publishDate;
    private String summary;


}
