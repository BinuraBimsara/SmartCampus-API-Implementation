package com.smartcampus.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception Mapper that turns a thrown SensorUnavailableException into a JSON Error Response
 * with HTTP 403 Forbidden.
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", "Forbidden");
        errorBody.put("message", exception.getMessage());
        errorBody.put("status", 403);

        return Response.status(Response.Status.FORBIDDEN)
                .entity(errorBody)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
