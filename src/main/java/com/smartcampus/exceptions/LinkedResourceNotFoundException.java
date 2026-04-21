package com.smartcampus.exceptions;

/**
 * Custom Exception mapped to HTTP 422 Unprocessable Entity.
 * Scenario: A client attempts to POST a new Sensor with a roomId that does not exist.
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}
