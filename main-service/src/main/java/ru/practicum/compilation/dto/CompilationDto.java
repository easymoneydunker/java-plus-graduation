package ru.practicum.compilation.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import ru.practicum.event.dto.EventShortDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    @NotNull
    private Long id;
    @Nullable
    private List<EventShortDto> events;
    @NotNull
    private Boolean pinned;
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
}
