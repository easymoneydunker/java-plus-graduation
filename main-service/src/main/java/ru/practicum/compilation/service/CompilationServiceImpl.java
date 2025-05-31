package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import ru.practicum.common.NotFoundException;
import ru.practicum.common.PageableBuilder;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationsRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationsRepository compilationsRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    private static void assertCompletion(List<Long> eventIds, List<Event> events) throws BadRequestException {
        if (eventIds.size() != events.size()) {
            eventIds.removeAll(events.stream().map(Event::getId).toList());
            throw new BadRequestException("Events with ids {" + eventIds + "} do not exist");
        }
    }

    @Override
    public List<CompilationDto> getAll(boolean pinned, int from, int size) {
        log.debug("Fetching compilations with pinned={} from={} size={}", pinned, from, size);
        var pageable = PageableBuilder.getPageable(from, size, "id");
        var compilationPage = compilationsRepository.findByPinned(pinned, pageable);
        var compilations = compilationPage.getContent();

        log.debug("Found {} compilations", compilations.size());
        return compilations.isEmpty() ? Collections.emptyList() : compilations.stream().map(compilationMapper::toCompilationDto).toList();
    }

    @Override
    public CompilationDto get(Long id) {
        log.debug("Fetching compilation with id={}", id);
        return compilationsRepository.findById(id).map(compilationMapper::toCompilationDto).orElse(null);
    }

    @Override
    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto) throws BadRequestException {
        log.debug("Adding new compilation with data: {}", newCompilationDto);
        var compilationBuilder = Compilation.builder().pinned(newCompilationDto.getPinned());
        var eventIds = newCompilationDto.getEvents();

        if (eventIds != null) {
            List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            assertCompletion(eventIds, events);
            compilationBuilder.events(events);
        }

        Optional.ofNullable(newCompilationDto.getTitle()).ifPresent(compilationBuilder::title);

        var result = compilationsRepository.saveAndFlush(compilationBuilder.build());
        log.info("Successfully added compilation with id={}", result.getId());
        return compilationMapper.toCompilationDto(result);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting compilation with id={}", id);
        var compilation = getOrElseThrow(id);
        compilationsRepository.delete(compilation);
        log.info("Successfully deleted compilation with id={}", id);
    }

    public Compilation getOrElseThrow(Long id) {
        log.debug("Fetching compilation or throwing exception for id={}", id);
        return compilationsRepository.findById(id).orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
    }

    @Override
    public CompilationDto update(Long id, UpdateCompilationRequest request) throws BadRequestException {
        log.debug("Updating compilation with id={} using data: {}", id, request);
        var compilation = getOrElseThrow(id);
        var updateBuilder = compilation.toBuilder();
        var eventIds = request.getEvents();

        if (eventIds != null) {
            var events = eventRepository.findAllByIdIn(eventIds);
            assertCompletion(eventIds, events);
            updateBuilder.events(events);
        }

        if (request.getPinned() != null) {
            updateBuilder.pinned(request.getPinned());
        }

        if (request.getTitle() != null) {
            updateBuilder.title(request.getTitle());
        }

        var result = compilationsRepository.saveAndFlush(updateBuilder.build());
        log.info("Successfully updated compilation with id={}", id);
        return compilationMapper.toCompilationDto(result);
    }
}
