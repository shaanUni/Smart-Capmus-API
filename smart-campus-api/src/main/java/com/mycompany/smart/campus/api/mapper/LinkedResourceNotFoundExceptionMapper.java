package com.mycompany.smart.campus.api.mapper;

import com.mycompany.smart.campus.api.exception.LinkedResourceNotFoundException;
import com.mycompany.smart.campus.api.model.ApiError;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        ApiError error = new ApiError(
                422,
                "LINKED_RESOURCE_NOT_FOUND",
                ex.getMessage()
        );

        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}