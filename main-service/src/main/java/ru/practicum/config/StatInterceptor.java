package ru.practicum.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.practicum.statistic.service.StatService;

@Component
@AllArgsConstructor
@Slf4j
public class StatInterceptor implements HandlerInterceptor {
    private final StatService statService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        statService.createStats(request.getRequestURI(), request.getRemoteAddr());
        log.info("process statistic for {}", request.getRequestURI());
    }
}
