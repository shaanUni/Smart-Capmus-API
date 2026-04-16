package com.mycompany.smart.campus.api.exception;

public class SensorUnavailableException extends RuntimeException {

    public SensorUnavailableException(String message) {
        super(message);
    }
}