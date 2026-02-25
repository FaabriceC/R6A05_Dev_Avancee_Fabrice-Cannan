package com.master.air.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(int status, String error, String message, List<String> details, String timestamp) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, null, Instant.now().toString());
    }
    public ErrorResponse(int status, String error, String message, List<String> details) {
        this(status, error, message, details, Instant.now().toString());
    }
}
