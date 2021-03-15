package com.aldren.exception;

public class BadRequestException extends Exception {

    private String message;

    public BadRequestException(String message) {
        this.message = message;
    }

}
