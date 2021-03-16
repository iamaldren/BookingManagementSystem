package com.aldren.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookResponse {

    private String bookId;
    private String bookName;
    private String toBeReturnedDate;
    private String returnedDate;
    private String remarks;

}
