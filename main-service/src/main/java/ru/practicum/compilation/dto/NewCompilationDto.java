package ru.practicum.compilation.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    @Nullable
    private List<Long> events;
    @Builder.Default
    private Boolean pinned = false;
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
}

