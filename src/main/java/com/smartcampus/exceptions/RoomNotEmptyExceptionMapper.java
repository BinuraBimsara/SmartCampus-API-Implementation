package com.smartcampus.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception Mapper that turns a thrown RoomNotEmptyException into a JSON Error Response
 * with HTTP 409 Conflict.
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", "Conflict");
        errorBody.put("message", exception.getMessage());
        errorBody.put("status", 409);

        return Response.status(Response.Status.CONFLICT)
                .entity(errorBody)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
