package com.aldren.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@RedisHash("Borrowed")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Borrowed {

    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String bookId;
    private String borrowedDate;
    private String expiryDate;
    @Indexed
    private String returnedDate;

}
