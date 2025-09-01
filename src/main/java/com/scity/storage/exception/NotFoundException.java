package com.scity.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public NotFoundException(String objectName, Object value) {
        super("Not found " + objectName + " with id: " + value);
    }

    public NotFoundException(String objectName, String fieldName, Object value) {
        super("Not found " + objectName + " with " + fieldName + ": " + value);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
