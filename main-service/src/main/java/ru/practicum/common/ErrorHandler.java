package ru.practicum.common;

import java.io.PrintWriter;
import java.io.StringWriter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        log.error("500 {}", e.getMessage(), e);
        String stackTrace = getStackTrace(e);
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred", e.getMessage(), stackTrace);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentNotValidException.class,
            ValidationException.class, HttpMessageNotReadableException.class, HandlerMethodValidationException.class,
            IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final Exception e) {
        log.error("{} {}", HttpStatus.BAD_REQUEST, e.getMessage(), e);
        return new ApiError(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage(), getStackTrace(e));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final Exception e) {
        log.error("{} {}", HttpStatus.NOT_FOUND, e.getMessage(), e);
        return new ApiError(HttpStatus.NOT_FOUND, "The required object was not found.", e.getMessage(),
                getStackTrace(e));
    }


    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final Exception e) {
        log.error("{} {}", HttpStatus.CONFLICT, e.getMessage(), e);
        return new ApiError(HttpStatus.CONFLICT, "Integrity constraint has been violated.", e.getMessage(),
                getStackTrace(e));
    }
}
