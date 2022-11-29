package ru.practicum.ewm.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.entities.ParticipationRequest;

@Component
public class ParticipationRequestMapper {
    public ParticipationRequestDto toDto(ParticipationRequest pr) {
        return new ParticipationRequestDto(pr.getCreated(), pr.getEventId(),
                pr.getId(), pr.getRequesterId(), pr.getStatus());
    }
}
