package com.master.air.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private List<String> details;
    private String timestamp;

    public ErrorResponse() {}

    private ErrorResponse(Builder builder) {
        this.status = builder.status;
        this.error = builder.error;
        this.message = builder.message;
        this.details = builder.details;
        this.timestamp = Instant.now().toString();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private int status;
        private String error;
        private String message;
        private List<String> details;

        public Builder status(int status) { this.status = status; return this; }
        public Builder error(String error) { this.error = error; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder details(List<String> details) { this.details = details; return this; }
        public ErrorResponse build() { return new ErrorResponse(this); }
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public List<String> getDetails() { return details; }
    public String getTimestamp() { return timestamp; }

    public void setStatus(int status) { this.status = status; }
    public void setError(String error) { this.error = error; }
    public void setMessage(String message) { this.message = message; }
    public void setDetails(List<String> details) { this.details = details; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
