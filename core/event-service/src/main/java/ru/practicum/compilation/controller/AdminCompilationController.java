package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

@RestController
@RequestMapping("/admin/compilations")
@AllArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addNewCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) throws BadRequestException {
        return compilationService.addNewCompilation(newCompilationDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long id) {
        compilationService.delete(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable Long id,
                                            @RequestBody @Valid UpdateCompilationRequest request) throws BadRequestException {
        return compilationService.update(id, request);
    }


}
