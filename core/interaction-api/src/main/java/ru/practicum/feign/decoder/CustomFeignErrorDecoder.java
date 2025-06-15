package ru.practicum.feign.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.common.ConflictException;
import ru.practicum.common.NotFoundException;
import ru.practicum.common.ValidationException;

import java.io.IOException;

@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "Feign error: status " + response.status() + ", method " + methodKey;
        String body = "";

        try {
            if (response.body() != null) {
                body = new String(response.body().asInputStream().readAllBytes());
                message += ", body: " + body;
            }
        } catch (IOException e) {
            log.warn("Failed to read error response body", e);
        }

        log.error(message);

        return switch (response.status()) {
            case 400 -> new ValidationException("Bad request: " + body);
            case 404 -> new NotFoundException("Resource not found: " + body);
            case 409 -> new ConflictException("Conflict occurred: " + body);
            case 500 -> new RuntimeException("Internal server error: " + body);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}