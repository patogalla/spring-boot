package com.patogalla.api.utils.error;

import com.google.common.base.Preconditions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerErrorHandler {

    public static final String VALIDATION_PROBLEM_TITLE = "There are some validation error(s) while processing the request";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleNotValidArguments(final MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(convert(ex, VALIDATION_PROBLEM_TITLE));
    }

    /* Malformed params are considered as not found resources */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleConversionError() {
    }


    public static Problem convert(final MethodArgumentNotValidException ex, final String message) {
        final BindingResult result = Preconditions.checkNotNull(ex.getBindingResult());
        final List<FieldError> fieldErrors = result.getFieldErrors();

        final List<InvalidParam> fieldValidationErrors = fieldErrors.stream().map(fieldError ->
                new InvalidParam(fieldError.getField(), fieldError.getDefaultMessage(), ValidationMessages.reason(fieldError.getDefaultMessage()))
        ).collect(Collectors.toList());

        return new Problem(message, null, fieldValidationErrors);
    }

}
