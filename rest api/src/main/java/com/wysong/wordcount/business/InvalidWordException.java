package com.wysong.wordcount.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception that is thrown when a word is deemed invalid.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidWordException extends RuntimeException {
    public InvalidWordException(String word) {
        super("PUT requests must be one word in length.");
    }
}
