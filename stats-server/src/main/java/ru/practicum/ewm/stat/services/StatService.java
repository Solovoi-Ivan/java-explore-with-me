package ru.practicum.ewm.stat.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.stat.entities.Hit;
import ru.practicum.ewm.stat.repositories.StatRepository;
import ru.practicum.ewm.stat.util.JsonConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatService {
    private final StatRepository statRepository;

    public void addEndpointHit(EndpointHit e) {
        statRepository.save(new Hit(e.getApp(), e.getUri(), e.getIp(), e.getTimestamp()));
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(JsonConstants.pattern);
        if (unique) {
            return statRepository.getUniqueViews(LocalDateTime.parse(start, formatter),
                            LocalDateTime.parse(end, formatter)).stream()
                    .map(o -> new ViewStats(o.get(0).toString(), o.get(1).toString(),
                            Long.parseLong(o.get(2).toString())))
                    .filter(v -> uris == null || uris.stream()
                            .anyMatch(u -> v.getUri().equals(u)))
                    .collect(Collectors.toList());
        } else {
            return statRepository.getViews(LocalDateTime.parse(start, formatter),
                            LocalDateTime.parse(end, formatter)).stream()
                    .filter(v -> uris == null || uris.stream()
                            .anyMatch(u -> v.getUri().equals(u)))
                    .collect(Collectors.toList());
        }
    }
}
