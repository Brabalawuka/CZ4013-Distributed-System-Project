package com.company.cz4013.exception;

/**
 * Exception for not being able confirm the client's status
 */
public class ClientDisconnectionException extends Exception{

    public ClientDisconnectionException(String msg) {
        super(msg);
    }

}
