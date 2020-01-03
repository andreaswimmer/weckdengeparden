package com.gepardec.wdg.challenge.exception;

import com.gepardec.wdg.application.exception.ExceptionHandledEvent;
import com.gepardec.wdg.challenge.model.ConstraintViolationResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@ApplicationScoped
@Provider
public class ConstraintValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    UriInfo uriInfo;

    @Inject
    Logger log;

    @Inject
    Event<ExceptionHandledEvent> handledEvent;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        log.info("Constraint violation(s) on resource: '{}'", uriInfo.getPath());
        final Response response = Response.status(HttpStatus.SC_BAD_REQUEST).entity(ConstraintViolationResponse.invalid("The request was invalid due to constraint violations", exception.getConstraintViolations())).build();
        handledEvent.fire(ExceptionHandledEvent.Builder.newBuilder(exception).withIsError(false).build());
        return response;
    }
}
