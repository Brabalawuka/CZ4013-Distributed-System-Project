package com.company.cz4013.exception;

/**
 * Exception for not being able to deserialize the data
 */
public class DeserialisationError extends Exception{

    public DeserialisationError(String message) {
        super(message);
    }
}
