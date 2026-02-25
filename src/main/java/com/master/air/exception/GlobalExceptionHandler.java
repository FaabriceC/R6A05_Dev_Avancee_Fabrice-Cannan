package com.master.air.exception;

import com.master.air.dto.ErrorResponse;
import jakarta.persistence.OptimisticLockException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse(404, "Not Found", e.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException e) {
        return ResponseEntity.status(409).body(new ErrorResponse(409, "Conflict", e.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException e) {
        return ResponseEntity.status(403).body(new ErrorResponse(403, "Forbidden", e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(403).body(new ErrorResponse(403, "Forbidden", "Acces refuse : role insuffisant"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage()).toList();
        return ResponseEntity.badRequest().body(new ErrorResponse(400, "Bad Request", "Erreur de validation", details));
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockException e) {
        return ResponseEntity.status(409).body(new ErrorResponse(409, "Conflict", "Conflit de concurrence"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArg(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(400, "Bad Request", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse(500, "Internal Server Error", "Erreur interne"));
    }
}
