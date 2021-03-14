package com.aldren.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class BookUserMapper {

    private long bookId;
    private long userId;
    private String borrowedDate;
    private String returnedDate;
    private String expiryDate;

}
