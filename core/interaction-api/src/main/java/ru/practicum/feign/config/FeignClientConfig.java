package ru.practicum.feign.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import ru.practicum.feign.decoder.CustomFeignErrorDecoder;

public class FeignClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }
}
