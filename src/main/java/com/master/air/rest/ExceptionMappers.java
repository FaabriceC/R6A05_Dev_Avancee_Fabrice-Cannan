package com.master.air.rest;

import com.master.air.dto.ErrorResponse;
import com.master.air.exception.ApiException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class ExceptionMappers {

    @Provider
    public static class ApiExceptionMapper implements ExceptionMapper<ApiException> {
        private static final Logger log = LoggerFactory.getLogger(ApiExceptionMapper.class);

        @Override
        public Response toResponse(ApiException e) {
            log.warn("ApiException {}: {}", e.getStatusCode(), e.getMessage());
            return Response.status(e.getStatusCode())
                    .entity(ErrorResponse.builder()
                            .status(e.getStatusCode())
                            .error(Response.Status.fromStatusCode(e.getStatusCode()).getReasonPhrase())
                            .message(e.getMessage())
                            .build())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @Provider
    public static class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
        @Override
        public Response toResponse(ConstraintViolationException e) {
            List<String> details = e.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();

            return Response.status(400)
                    .entity(ErrorResponse.builder()
                            .status(400)
                            .error("Bad Request")
                            .message("Erreur de validation")
                            .details(details)
                            .build())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @Provider
    public static class GenericExceptionMapper implements ExceptionMapper<Exception> {
        private static final Logger log = LoggerFactory.getLogger(GenericExceptionMapper.class);

        @Override
        public Response toResponse(Exception e) {
            log.error("Erreur interne non interceptee", e);
            return Response.status(500)
                    .entity(ErrorResponse.builder()
                            .status(500)
                            .error("Internal Server Error")
                            .message("Une erreur interne est survenue")
                            .build())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
