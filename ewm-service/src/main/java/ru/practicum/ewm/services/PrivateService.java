package ru.practicum.ewm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entities.*;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.mappers.EventMapper;
import ru.practicum.ewm.mappers.ParticipationRequestMapper;
import ru.practicum.ewm.repositories.UserEventRatingRepository;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.repositories.ParticipationRequestRepository;
import ru.practicum.ewm.repositories.UserRepository;
import ru.practicum.ewm.util.EventState;
import ru.practicum.ewm.util.RequestStatus;
import ru.practicum.ewm.util.UserEventRatingId;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final UserEventRatingRepository userEventRatingRepository;
    private final EventMapper eventMapper;
    private final ParticipationRequestMapper requestMapper;
    private final PublicService publicService;

    public List<EventShortDto> getAllUserEvents(int userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id"));
        return eventRepository.findByInitiatorId(userId, pageRequest).stream()
                .map(publicService::getEventShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEvent(UpdateEventRequest updateEventRequest, int userId) {
        Event event = eventRepository.findById(updateEventRequest.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Событие " +
                        updateEventRequest.getEventId() + " не найдено"));
        if (!event.getInitiatorId().equals(userId)) {
            throw new RuntimeException("Редактировать событие может только его организатор");
        }

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new RuntimeException("Нельзя редактировать опубликованные события");
        }

        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }

        if (updateEventRequest.getCategory() != null) {
            event.setCategoryId(updateEventRequest.getCategory());
        }

        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }

        if (updateEventRequest.getEventDate() != null) {
            if (updateEventRequest.getEventDate().minusHours(2).isAfter(LocalDateTime.now())) {
                event.setEventDate(updateEventRequest.getEventDate());
            } else {
                throw new ValidationException("Событие должно начинаться не раньше, чем через два часа");
            }
        }

        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }

        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }

        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }

        if (event.getState().equals(EventState.CANCELED)) {
            event.setState(EventState.PENDING);
        }

        return publicService.getEventFullDto(eventRepository.save(event));
    }

    public EventFullDto createEvent(NewEventDto newEventDto, int userId) {
        if (newEventDto.getEventDate().minusHours(2).isAfter(LocalDateTime.now())) {
            Event event = eventMapper.fromDto(newEventDto, LocalDateTime.now(), userId, EventState.PENDING);
            return publicService.getEventFullDto(eventRepository.save(event));
        } else {
            throw new ValidationException("Событие должно начинаться не раньше, чем через два часа");
        }
    }

    public EventFullDto getUserEventById(int userId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));
        if (event.getInitiatorId().equals(userId)) {
            return publicService.getEventFullDto(event);
        } else {
            throw new RuntimeException("Пользователь " + userId + " не добавлял такого события");
        }
    }

    public EventFullDto cancelUserEvent(int userId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));
        if (event.getInitiatorId().equals(userId)) {
            if (event.getState().equals(EventState.PENDING)) {
                event.setState(EventState.CANCELED);
                eventRepository.save(event);
                return publicService.getEventFullDto(event);
            } else {
                throw new RuntimeException("Можно отменить только событие, ожидающее модерации");
            }
        } else {
            throw new RuntimeException("Пользователь " + userId + " не добавлял такого события");
        }
    }

    public List<ParticipationRequestDto> getEventParticipationRequests(int userId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));
        if (!event.getInitiatorId().equals(userId)) {
            throw new RuntimeException("Пользователь " + userId + " не добавлял такого события");
        }
        return requestRepository.findByEventId(eventId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto approveParticipationRequest(int userId, int eventId, int reqId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));
        ParticipationRequest request = requestRepository.findById(reqId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на участие " + reqId + " не найден"));

        if (!event.getInitiatorId().equals(userId)) {
            throw new RuntimeException("Неверно указан автор события");
        }

        participantLimitValidation(event);

        if (event.getParticipantLimit() - publicService.getConfirmedRequests(event) != 1
                && event.getParticipantLimit() != 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            return requestMapper.toDto(requestRepository.save(request));
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
            requestRepository.save(request);
            requestRepository.findByEventIdAndStatus(eventId, RequestStatus.PENDING).stream()
                    .peek(r -> r.setStatus(RequestStatus.REJECTED))
                    .forEach(requestRepository::save);
            return requestMapper.toDto(request);
        }
    }

    public ParticipationRequestDto rejectParticipationRequest(int userId, int eventId, int reqId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));
        ParticipationRequest request = requestRepository.findById(reqId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на участие " + reqId + " не найден"));

        if (!event.getInitiatorId().equals(userId)) {
            throw new RuntimeException("Неверно указан автор события");
        }

        if (!request.getEventId().equals(eventId)) {
            throw new RuntimeException("Неверно указано событие");
        }

        if (request.getStatus().equals(RequestStatus.PENDING)) {
            request.setStatus(RequestStatus.REJECTED);
            return requestMapper.toDto(requestRepository.save(request));
        } else {
            throw new RuntimeException("Запрос " + reqId + " не находится на рассмотрении");
        }
    }

    public List<ParticipationRequestDto> getUserParticipationRequests(int userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto createParticipationRequest(int userId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден"));

        if (event.getInitiatorId().equals(user.getId())) {
            throw new RuntimeException("Нельзя участвовать в собственном событии");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new RuntimeException("Нельзя участвовать в неопубликованном событии");
        }

        if (!requestRepository.findByRequesterIdAndEventId(user.getId(), eventId).isEmpty()) {
            throw new RuntimeException("Нельзя отправить повторный запрос");
        }

        participantLimitValidation(event);

        ParticipationRequest request = new ParticipationRequest(LocalDateTime.now(),
                eventId, user.getId(), RequestStatus.PENDING);

        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return requestMapper.toDto(requestRepository.save(request));
    }

    public ParticipationRequestDto cancelParticipationRequest(int userId, int requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на участие " + requestId + " не найден"));
        if (!request.getRequesterId().equals(userId)) {
            throw new RuntimeException("Неверно указан автор запроса на участие");
        }

        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toDto(requestRepository.save(request));
    }

    public EventShortDto addEventLike(int userId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден"));
        UserEventRating userEventRating = ratingValidation(user, event);
        if (userEventRating == null) {
            userEventRatingRepository.save(new UserEventRating(userId, eventId, true));
        } else {
            if (userEventRating.getIsPositive()) {
                throw new ValidationException("Этот пользователь уже оценивал событие");
            } else {
                userEventRating.setIsPositive(true);
                userEventRatingRepository.save(userEventRating);
            }
        }

        return publicService.getEventShortDto(event);
    }

    public EventShortDto addEventDislike(int userId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден"));
        UserEventRating userEventRating = ratingValidation(user, event);
        if (userEventRating == null) {
            userEventRatingRepository.save(new UserEventRating(userId, eventId, false));
        } else {
            if (!userEventRating.getIsPositive()) {
                throw new ValidationException("Этот пользователь уже оценивал событие");
            } else {
                userEventRating.setIsPositive(false);
                userEventRatingRepository.save(userEventRating);
            }
        }

        return publicService.getEventShortDto(event);
    }

    public EventShortDto removeEventLike(int userId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден"));
        UserEventRating userEventRating = ratingValidation(user, event);
        if (userEventRating == null) {
            throw new ValidationException("Этот пользователь не ставил лайк событию");
        } else {
            if (userEventRating.getIsPositive()) {
                userEventRatingRepository.deleteById(new UserEventRatingId(user.getId(), event.getId()));
            } else {
                throw new ValidationException("Этот пользователь не ставил лайк событию");
            }
        }

        return publicService.getEventShortDto(event);
    }

    public EventShortDto removeEventDislike(int userId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден"));
        UserEventRating userEventRating = ratingValidation(user, event);
        if (userEventRating == null) {
            throw new ValidationException("Этот пользователь не ставил дислайк событию");
        } else {
            if (userEventRating.getIsPositive()) {
                throw new ValidationException("Этот пользователь не ставил дислайк событию");
            } else {
                userEventRatingRepository.deleteById(new UserEventRatingId(user.getId(), event.getId()));
            }
        }

        return publicService.getEventShortDto(event);
    }

    private void participantLimitValidation(Event e) {
        if (e.getParticipantLimit() - publicService.getConfirmedRequests(e) == 0 && e.getParticipantLimit() != 0) {
            throw new RuntimeException("Превышен лимит количества участников");
        }
    }

    private UserEventRating ratingValidation(User user, Event event) {
        UserEventRating userEventRating = userEventRatingRepository
                .findById(new UserEventRatingId(user.getId(), event.getId()))
                .orElse(null);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Нельзя оценивать неопубликованное событие");
        }

        if (event.getInitiatorId().equals(user.getId())) {
            throw new ValidationException("Нельзя оценивать собственное событие");
        }

        if (event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Нельзя оценивать событие, которое ещё не произошло");
        }

        if (getUserParticipationRequests(user.getId()).stream()
                .noneMatch(p -> p.getEvent().equals(event.getId()) && p.getStatus().equals(RequestStatus.CONFIRMED))) {
            throw new ValidationException("Нельзя оценивать событие, которое вы не посетили");
        }
        return userEventRating;
    }
}