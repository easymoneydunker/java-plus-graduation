package ru.practicum.enums;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("user-action-weight")
public class UserActionWeight {
    private double VIEW;
    private double REGISTER;
    private double LIKE;
}