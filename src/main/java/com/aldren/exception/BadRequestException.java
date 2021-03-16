package com.aldren.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends Exception {

    private String message;

    public BadRequestException(String message) {
        this.message = message;
    }

}
