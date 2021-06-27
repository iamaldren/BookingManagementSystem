package com.aldren.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "book")
public class BookProperties {

    private int maximumBorrowedByUser;
    private int borrowDuration;
    private long penalty;

}
