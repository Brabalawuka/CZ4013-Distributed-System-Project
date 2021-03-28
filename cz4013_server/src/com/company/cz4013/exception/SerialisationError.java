package com.company.cz4013.exception;

/**
 * Exception for not being able to serialize the data
 */
public class SerialisationError extends Exception{

    public SerialisationError(String message) {
        super(message);
    }
}
