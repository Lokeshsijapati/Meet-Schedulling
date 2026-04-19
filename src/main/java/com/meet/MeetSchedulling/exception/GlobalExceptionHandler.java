package com.meet.MeetSchedulling.exception;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

        // 404
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFound(
                        ResourceNotFoundException ex,
                        HttpServletRequest request) {

                return new ResponseEntity<>(
                                new ErrorResponse(
                                                LocalDateTime.now(),
                                                HttpStatus.NOT_FOUND.value(),
                                                "RESOURCE_NOT_FOUND",
                                                ex.getMessage(),
                                                request.getRequestURI()),
                                HttpStatus.NOT_FOUND);
        }

        // 400 — Custom Bad Request
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponse> handleBadRequest(
                        BadRequestException ex,
                        HttpServletRequest request) {
                return new ResponseEntity<>(
                                new ErrorResponse(
                                                LocalDateTime.now(),
                                                HttpStatus.BAD_REQUEST.value(),
                                                "BAD_REQUEST",
                                                ex.getMessage(),
                                                request.getRequestURI()),
                                HttpStatus.BAD_REQUEST);
        }

        // 400 — Validation
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidation(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                                .getFieldErrors()
                                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

                return ResponseEntity.badRequest().body(errors);
        }

   // 400 — Same Date and Time
        @ExceptionHandler(SameDateTimeException.class)
        public ResponseEntity<ErrorResponse> handleSameDateTime(
                        SameDateTimeException ex,
                        HttpServletRequest request) {


                return new ResponseEntity<>(
                                new ErrorResponse(
                                                LocalDateTime.now(),
                                                HttpStatus.CONFLICT.value(),
                                                "SAME_DATE_TIME",
                                                ex.getMessage(),
                                                request.getRequestURI()),
                                HttpStatus.CONFLICT);
        }


}
