package com.aldren.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@RedisHash("Book")
public class Book {

    private String id;
    @Indexed
    private String isbn;
    @Indexed
    private String name;
    private String author;
    private String publishDate;
    private String summary;


}
