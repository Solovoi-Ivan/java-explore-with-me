package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entities.ParticipationRequest;
import ru.practicum.ewm.util.RequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    List<ParticipationRequest> findByRequesterIdAndEventId(int requesterId, int eventId);

    List<ParticipationRequest> findByEventIdAndStatus(int eventId, RequestStatus requestStatus);

    List<ParticipationRequest> findByEventId(int eventId);

    List<ParticipationRequest> findByRequesterId(int requesterId);
}
