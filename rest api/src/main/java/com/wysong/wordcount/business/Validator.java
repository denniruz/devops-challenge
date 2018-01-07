package com.wysong.wordcount.business;

public interface Validator<T> {

    /**
     * Generic validation method.
     *
     * @param object the object to validate.
     * @throws RuntimeException if there is a validation error.
     */
    void validate(T object) throws RuntimeException;
}
