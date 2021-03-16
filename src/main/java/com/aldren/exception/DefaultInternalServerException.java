package com.aldren.exception;

import lombok.Getter;

@Getter
public class DefaultInternalServerException extends Exception {

    private String message;

    public DefaultInternalServerException(String message) {
        this.message = message;
    }

}
