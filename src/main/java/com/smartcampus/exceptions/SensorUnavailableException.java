package com.smartcampus.exceptions;

/**
 * Custom Exception mapped to HTTP 403 Forbidden.
 * Scenario: Attempting to POST a reading to a Sensor marked as "MAINTENANCE" or "OFFLINE".
 */
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}
