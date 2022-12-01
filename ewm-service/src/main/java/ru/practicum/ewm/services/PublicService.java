package ru.practicum.ewm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entities.Compilation;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.mappers.CategoryMapper;
import ru.practicum.ewm.mappers.CompilationMapper;
import ru.practicum.ewm.mappers.EventMapper;
import ru.practicum.ewm.mappers.UserMapper;
import ru.practicum.ewm.repositories.*;
import ru.practicum.ewm.util.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicService {
    private final StatsClient statsClient;
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public List<EventShortDto> getAllEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                            String rangeEnd, Boolean onlyAvailable, String sort, int from, int size) {
        List<Event> eventList;

        if (text != null) {
            eventList = eventRepository.search(text);
        } else {
            eventList = eventRepository.findByState(EventState.PUBLISHED);
        }

        eventList = eventList.stream()
                .filter(e -> categories == null || categories.stream()
                        .anyMatch(i -> e.getCategoryId().equals(i)))
                .filter(e -> paid == null || e.getPaid().equals(paid))
                .filter(e -> rangeStart == null || e.getEventDate()
                        .isAfter(LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(JsonConstants.pattern))))
                .filter(e -> rangeEnd == null || e.getEventDate()
                        .isBefore(LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(JsonConstants.pattern))))
                .filter(e -> rangeStart != null || rangeEnd != null || e.getEventDate().isAfter(LocalDateTime.now()))
                .filter(e -> !onlyAvailable || e.getParticipantLimit() - getConfirmedRequests(e) > 0)
                .collect(Collectors.toList());

        List<EventShortDto> eventListDto = eventList.stream()
                .map(this::getEventShortDto)
                .collect(Collectors.toList());

        if (sort != null && EventSort.valueOf(sort).equals(EventSort.VIEWS)) {
            eventListDto = eventListDto.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews))
                    .collect(Collectors.toList());
        } else if (sort != null && EventSort.valueOf(sort).equals(EventSort.EVENT_DATE)) {
            eventListDto = eventListDto.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .collect(Collectors.toList());
        }
        statsClient.addEndpointHit("app", "/events", "ip");
        return ListPagination.getPage(eventListDto, from / size, size);
    }

    public EventFullDto getEventById(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие " + eventId + " не найдено"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            statsClient.addEndpointHit("app", "/events/" + eventId, "ip");
            return getEventFullDto(event);
        } else {
            throw new RuntimeException("Событие " + eventId + " не опубликовано");
        }
    }

    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        PageRequest pr = PageRequest.of(from / size, size, Sort.by("id"));
        if (pinned == null) {
            return compilationRepository.findAll(pr).stream()
                    .map(c -> compilationMapper.toDto(c, c.getEvents().stream()
                            .map(this::getEventShortDto)
                            .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        } else {
            return compilationRepository.findByPinned(pinned, pr).stream()
                    .map(c -> compilationMapper.toDto(c, c.getEvents().stream()
                            .map(this::getEventShortDto)
                            .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }
    }

    public CompilationDto getCompilationById(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка " + compId + " не найдена"));
        return compilationMapper.toDto(compilation, compilation.getEvents().stream()
                .map(this::getEventShortDto)
                .collect(Collectors.toList()));
    }

    public List<CategoryDto> getAllCategories(int from, int size) {
        PageRequest pr = PageRequest.of(from / size, size, Sort.by("id"));
        return categoryRepository.findAll(pr).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(int catId) {
        return getCategoryDtoById(catId);
    }

    public EventFullDto getEventFullDto(Event event) {
        return eventMapper.toDto(event, getCategoryDtoById(event.getCategoryId()),
                getConfirmedRequests(event), getUserShortDtoById(event.getInitiatorId()), getViews(event.getId()));
    }

    public EventShortDto getEventShortDto(Event event) {
        CategoryDto catDto = getCategoryDtoById(event.getCategoryId());
        UserShortDto userDto = getUserShortDtoById(event.getInitiatorId());
        return eventMapper.toShortDto(event, catDto, getConfirmedRequests(event), userDto, getViews(event.getId()));
    }

    public CategoryDto getCategoryDtoById(int catId) {
        return categoryMapper.toDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория " + catId + " не найдена")));
    }

    public UserShortDto getUserShortDtoById(int userId) {
        return userMapper.toShortDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь " + userId + " не найден")));
    }

    public int getConfirmedRequests(Event e) {
        return requestRepository.findByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED).size();
    }

    public int getViews(int eventId) {
        int views = 0;
        for (ViewStats v : statsClient.getStats(eventId)) {
            views += v.getHits();
        }

        return views;
    }
}