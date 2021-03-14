package com.aldren.exception;

import lombok.Getter;

@Getter
public class RecordNotFoundException extends Exception {

    private String message;

    public RecordNotFoundException(String message) {
        this.message = message;
    }

}
