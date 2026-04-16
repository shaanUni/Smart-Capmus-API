package com.mycompany.smart.campus.api.mapper;

import com.mycompany.smart.campus.api.exception.RoomNotEmptyException;
import com.mycompany.smart.campus.api.model.ApiError;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        ApiError error = new ApiError(
                409,
                "ROOM_NOT_EMPTY",
                ex.getMessage()
        );

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}