package com.smartcampus.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception Mapper that turns a thrown LinkedResourceNotFoundException into a JSON Error Response
 * with HTTP 422 Unprocessable Entity.
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", "Unprocessable Entity");
        errorBody.put("message", exception.getMessage());
        errorBody.put("status", 422);

        // JAX-RS standard enum doesn't strictly include 422 by default in older versions,
        // so we can build the status code directly via integer.
        return Response.status(422)
                .entity(errorBody)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
