package com.mycompany.smart.campus.api.mapper;

import com.mycompany.smart.campus.api.model.ApiError;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        if (ex instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) ex;
            int status = webEx.getResponse().getStatus();

            ApiError error = new ApiError(
                    status,
                    "REQUEST_ERROR",
                    webEx.getMessage() == null ? "Request failed." : webEx.getMessage()
            );

            return Response.status(status)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(error)
                    .build();
        }

        LOGGER.log(Level.SEVERE, "Unhandled exception", ex);

        ApiError error = new ApiError(
                500,
                "INTERNAL_SERVER_ERROR",
                "An unexpected server error occurred."
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}