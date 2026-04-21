package com.smartcampus.exceptions;

/**
 * Custom Exception mapped to HTTP 409 Conflict.
 * Scenario: Attempting to delete a Room that still has Sensors assigned to it.
 */
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}
