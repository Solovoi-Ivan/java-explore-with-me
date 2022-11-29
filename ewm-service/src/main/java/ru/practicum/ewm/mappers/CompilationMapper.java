package ru.practicum.ewm.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.entities.Compilation;
import ru.practicum.ewm.entities.Event;

import java.util.List;
import java.util.Set;

@Component
public class CompilationMapper {
    public CompilationDto toDto(Compilation c, List<EventShortDto> events) {
        return new CompilationDto(events, c.getId(), c.getPinned(), c.getTitle());
    }

    public Compilation fromDto(NewCompilationDto c, Set<Event> events) {
        return new Compilation(c.getPinned(), c.getTitle(), events);
    }
}
