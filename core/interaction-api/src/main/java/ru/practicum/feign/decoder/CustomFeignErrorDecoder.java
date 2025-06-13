package ru.practicum.feign.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "Feign error: status " + response.status() + ", method " + methodKey;
        try {
            if (response.body() != null) {
                String body = new String(response.body().asInputStream().readAllBytes());
                message += ", body: " + body;
            }
        } catch (Exception e) {
            log.warn("Failed to read error response body", e);
        }

        log.error(message);

        return defaultDecoder.decode(methodKey, response);
    }
}
