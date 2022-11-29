package ru.practicum.ewm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entities.Category;
import ru.practicum.ewm.entities.Compilation;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.mappers.CategoryMapper;
import ru.practicum.ewm.mappers.CompilationMapper;
import ru.practicum.ewm.mappers.UserMapper;
import ru.practicum.ewm.repositories.CategoryRepository;
import ru.practicum.ewm.repositories.CompilationRepository;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.repositories.UserRepository;
import ru.practicum.ewm.util.EventState;
import ru.practicum.ewm.util.ListPagination;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final CompilationMapper compilationMapper;
    private final PublicService publicService;

    public List<EventFullDto> getEvents(List<Integer> users, List<String> states, List<Integer> categories,
                                        String rangeStart, String rangeEnd, int from, int size) {
        List<Event> eventList = eventRepository.findAll().stream()

                .filter(e -> users == null || users.stream()
                        .anyMatch(i -> e.getInitiatorId().equals(i)))
                .filter(e -> states == null || states.stream()
                        .anyMatch(s -> e.getState().equals(EventState.valueOf(s))))
                .filter(e -> categories == null || categories.stream()
                        .anyMatch(c -> e.getCategoryId().equals(c)))
                .filter(e -> rangeStart == null || LocalDateTime.parse(rangeStart,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).isBefore(e.getEventDate()))
                .filter(e -> rangeEnd == null || LocalDateTime.parse(rangeEnd,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).isAfter(e.getEventDate()))
                .collect(Collectors.toList());

        return ListPagination.getPage(eventList, from / size, size).stream()
                .map(publicService::getEventFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEvent(AdminUpdateEventRequest updateEventRequest, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено"));
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
            event.setEventDate(updateEventRequest.getEventDate());
        }

        if (updateEventRequest.getLocation() != null) {
            event.setLat(updateEventRequest.getLocation().getLat());
            event.setLon(updateEventRequest.getLocation().getLon());
        }

        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }

        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }

        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }

        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }

        return publicService.getEventFullDto(eventRepository.save(event));
    }

    public EventFullDto publishEvent(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено"));
        if (event.getEventDate().minusHours(1).isBefore(LocalDateTime.now())) {
            throw new ValidationException("Событие должно начинаться не раньше, чем за час до публикации");
        }

        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Событие не ожидает публикации");
        }

        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        return publicService.getEventFullDto(eventRepository.save(event));
    }

    public EventFullDto rejectEvent(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено"));
        if (!event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Событие не ожидает публикации");
        }

        event.setState(EventState.CANCELED);
        return publicService.getEventFullDto(eventRepository.save(event));
    }

    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));
        category.setName(categoryDto.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.fromDto(newCategoryDto)));
    }

    public void deleteCategory(int catId) {
        if (eventRepository.findByCategoryId(catId).isEmpty()) {
            categoryRepository.deleteById(catId);
        } else {
            throw new RuntimeException("Данная категория используется");                                                 //TODO Протестировать
        }
    }

    public List<UserDto> getUsers(List<Integer> users, int from, int size) {
        return ListPagination.getPage(userRepository.findAll().stream()
                        .filter(u -> users == null || users.stream()
                                .anyMatch(i -> u.getId().equals(i)))
                        .map(userMapper::toDto)
                        .collect(Collectors.toList()),
                from / size, size);
    }

    public UserDto createUser(NewUserRequest userDto) {
        return userMapper.toDto(userRepository.save(userMapper.fromDto(userDto)));
    }

    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> events = newCompilationDto.getEvents().stream()
                .map(i -> eventRepository.findById(i)
                        .orElseThrow(() -> new EntityNotFoundException("Событие не найдено")))
                .collect(Collectors.toSet());

        List<EventShortDto> eventsDto = events.stream()
                .map(publicService::getEventShortDto)
                .collect(Collectors.toList());

        return compilationMapper.toDto(compilationRepository.save(compilationMapper
                .fromDto(newCompilationDto, events)), eventsDto);
    }

    public void deleteCompilation(int compId) {
        compilationRepository.deleteById(compId);
    }

    public void deleteEventFromCompilation(int compId, int eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка не найдена"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено"));
        if (compilation.getEvents().contains(event)) {
            compilation.getEvents().remove(event);
            compilationRepository.save(compilation);
        }
    }

    public void addEventToCompilation(int compId, int eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка не найдена"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено"));
        compilation.getEvents().add(event);
        compilationRepository.save(compilation);
    }

    public void deletePinFromCompilation(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка не найдена"));
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    public void addPinToCompilation(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка не найдена"));
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }
}