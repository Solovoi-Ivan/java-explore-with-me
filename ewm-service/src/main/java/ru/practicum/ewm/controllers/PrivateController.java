package ru.practicum.ewm.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.services.PrivateService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class PrivateController {
    private final PrivateService privateService;

    @GetMapping(path = "/{userId}/events")
    public List<EventShortDto> getAllUserEvents(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size,
            @PathVariable int userId) {
        log.info("Обработан GET-запрос /users/" + userId + "/events");
        return privateService.getAllUserEvents(userId, from, size);
    }

    @PatchMapping(path = "/{userId}/events")
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventRequest updateEventRequest,
                                    @PathVariable(name = "userId") int userId) {
        log.info("Обработан PATCH-запрос /users/" + userId + "/events");
        return privateService.updateEvent(updateEventRequest, userId);
    }

    @PostMapping(path = "/{userId}/events")
    public EventFullDto createEvent(@Valid @RequestBody NewEventDto newEventDto,
                                    @PathVariable(name = "userId") int userId) {
        log.info("Обработан POST-запрос /users/" + userId + "/events");
        return privateService.createEvent(newEventDto, userId);
    }

    @GetMapping(path = "/{userId}/events/{eventId}")
    public EventFullDto getUserEventById(@PathVariable(name = "userId") int userId,
                                         @PathVariable(name = "eventId") int eventId) {
        log.info("Обработан GET-запрос /users/" + userId + "/events/" + eventId);
        return privateService.getUserEventById(userId, eventId);
    }

    @PatchMapping(path = "/{userId}/events/{eventId}")
    public EventFullDto cancelUserEvent(@PathVariable(name = "userId") int userId,
                                        @PathVariable(name = "eventId") int eventId) {
        log.info("Обработан PATCH-запрос /users/" + userId + "/events/" + eventId);
        return privateService.cancelUserEvent(userId, eventId);
    }

    @GetMapping(path = "/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipationRequests(
            @PathVariable(name = "userId") int userId,
            @PathVariable(name = "eventId") int eventId) {
        log.info("Обработан GET-запрос /users/" + userId + "/events/" + eventId + "/requests");
        return privateService.getEventParticipationRequests(userId, eventId);
    }

    @PatchMapping(path = "/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto approveParticipationRequest(
            @PathVariable(name = "userId") int userId,
            @PathVariable(name = "eventId") int eventId,
            @PathVariable(name = "reqId") int reqId) {
        log.info("Обработан PATCH-запрос /users/" + userId + "/events/" + eventId + "/requests/" + reqId + "/confirm");
        return privateService.approveParticipationRequest(userId, eventId, reqId);
    }

    @PatchMapping(path = "/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectParticipationRequest(
            @PathVariable(name = "userId") int userId,
            @PathVariable(name = "eventId") int eventId,
            @PathVariable(name = "reqId") int reqId) {
        log.info("Обработан PATCH-запрос /users/" + userId + "/events/" + eventId + "/requests/" + reqId + "/reject");
        return privateService.rejectParticipationRequest(userId, eventId, reqId);
    }

    @GetMapping(path = "{userId}/requests")
    public List<ParticipationRequestDto> getUserParticipationRequests(@PathVariable(name = "userId") int userId) {
        log.info("Обработан GET-запрос /users/" + userId + "/requests");
        return privateService.getUserParticipationRequests(userId);
    }

    @PostMapping(path = "/{userId}/requests")
    public ParticipationRequestDto createParticipationRequest(@PathVariable(name = "userId") int userId,
                                                              @RequestParam(name = "eventId") int eventId) {
        log.info("Обработан POST-запрос /users/" + userId + "/requests");
        return privateService.createParticipationRequest(userId, eventId);
    }

    @PatchMapping(path = "/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable(name = "userId") int userId,
                                                              @PathVariable(name = "requestId") int requestId) {
        log.info("Обработан PATCH-запрос /users/" + userId + "/requests/" + requestId + "/cancel");
        return privateService.cancelParticipationRequest(userId, requestId);
    }

    @PatchMapping(path = "/{userId}/events/{eventId}/like")
    public EventShortDto addEventLike(@PathVariable(name = "userId") int userId,
                                      @PathVariable(name = "eventId") int eventId) {
        log.info("Обработан PATCH-запрос /users/" + userId + "/events/" + eventId + "/like");
        return privateService.addEventLike(userId, eventId);
    }

    @PatchMapping(path = "/{userId}/events/{eventId}/dislike")
    public EventShortDto addEventDislike(@PathVariable(name = "userId") int userId,
                                         @PathVariable(name = "eventId") int eventId) {
        log.info("Обработан PATCH-запрос /users/" + userId + "/events/" + eventId + "/dislike");
        return privateService.addEventDislike(userId, eventId);
    }

    @DeleteMapping(path = "/{userId}/events/{eventId}/like")
    public EventShortDto removeEventLike(@PathVariable(name = "userId") int userId,
                                         @PathVariable(name = "eventId") int eventId) {
        log.info("Обработан DELETE-запрос /users/" + userId + "/events/" + eventId + "/like");
        return privateService.removeEventLike(userId, eventId);
    }

    @DeleteMapping(path = "/{userId}/events/{eventId}/dislike")
    public EventShortDto removeEventDislike(@PathVariable(name = "userId") int userId,
                                            @PathVariable(name = "eventId") int eventId) {
        log.info("Обработан DELETE-запрос /users/" + userId + "/events/" + eventId + "/dislike");
        return privateService.removeEventDislike(userId, eventId);
    }
}