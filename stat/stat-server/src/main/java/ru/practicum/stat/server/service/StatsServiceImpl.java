package ru.practicum.stat.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;
import ru.practicum.stat.server.error.ValidationException;
import ru.practicum.stat.server.mapper.EndpointHitMapper;
import ru.practicum.stat.server.repository.EndpointHitEntityRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
    private final EndpointHitEntityRepository repository;
    private final EndpointHitMapper mapper;

    @Autowired
    public StatsServiceImpl(EndpointHitEntityRepository repository, EndpointHitMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStats> projections;

        if (end.isBefore(start)) {
            throw new ValidationException(String.format("End date %s is before start date %s", end, start));
        }

        if (uris != null && !uris.isEmpty()) {
            if (unique) {
                projections = repository.getUniqueStatsWithHitsAndUris(start, end, uris);
            } else {
                projections = repository.getStatsWithHitsAndUris(start, end, uris);
            }
        } else {
            if (unique) {
                projections = repository.getUniqueStatsWithHits(start, end);
            } else {
                projections = repository.getStatsWithHits(start, end);
            }
        }

        return projections;
    }

    @Override
    public EndpointHit saveHit(EndpointHit endpointHit) {
        return mapper.toDto(repository.save(mapper.toEntity(endpointHit)));
    }
}
