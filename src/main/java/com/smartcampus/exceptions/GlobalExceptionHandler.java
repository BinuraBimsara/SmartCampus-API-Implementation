package com.smartcampus.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Global Safety Net.
 * Intercepts any unexpected runtime exceptions and maps them to a seamless HTTP 500 Response,
 * preventing raw Java stack traces from leaking to the client.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        // If the exception is already a valid JAX-RS WebApplicationException (like 404 from Jersey router), 
        // we let it pass through naturally.
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        // Log the full stack trace internally to the server console safely
        LOGGER.log(Level.SEVERE, "Unexpected internal error occurred: ", exception);

        // Construct generic API error body
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", "Internal Server Error");
        errorBody.put("message", "An unexpected server condition occurred. Please contact the administrator.");
        errorBody.put("status", 500);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorBody)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
