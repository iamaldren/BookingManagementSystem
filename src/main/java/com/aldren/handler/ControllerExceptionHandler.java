package com.aldren.handler;

import com.aldren.exception.BadRequestException;
import com.aldren.exception.RecordNotFoundException;
import com.aldren.model.ErrorResponse;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @ExceptionHandler({RecordNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    ErrorResponse handleNotFoundException(Exception e, WebRequest u) {
        return ErrorResponse.builder()
                .timestamp(DateFormatUtils.format(new Date(), TIMESTAMP_FORMAT))
                .status(HttpStatus.NOT_FOUND.value())
                .description(HttpStatus.NOT_FOUND.name())
                .information(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ErrorResponse handleBadRequestException(Exception e, WebRequest u) {
        return ErrorResponse.builder()
                .timestamp(DateFormatUtils.format(new Date(), TIMESTAMP_FORMAT))
                .status(HttpStatus.BAD_REQUEST.value())
                .description(HttpStatus.BAD_REQUEST.name())
                .information(e.getLocalizedMessage())
                .build();
    }

}
