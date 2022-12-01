package ru.practicum.ewm.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.services.AdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping(path = "/events")
    public List<EventFullDto> getEvents(
            @RequestParam(name = "users", required = false) List<Integer> users,
            @RequestParam(name = "states", required = false) List<String> states,
            @RequestParam(name = "categories", required = false) List<Integer> categories,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Обработан GET-запрос (/events");
        return adminService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping(path = "/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody AdminUpdateEventRequest updateEventRequest,
                                    @PathVariable(name = "eventId") int eventId) {
        log.info("Обработан PUT-запрос /events/" + eventId);
        return adminService.updateEvent(updateEventRequest, eventId);
    }

    @PatchMapping(path = "/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable(name = "eventId") int eventId) {
        log.info("Обработан PATCH-запрос /events/" + eventId + "/publish");
        return adminService.publishEvent(eventId);
    }

    @PatchMapping(path = "/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable(name = "eventId") int eventId) {
        log.info("Обработан PATCH-запрос /events/" + eventId + "/reject");
        return adminService.rejectEvent(eventId);
    }

    @PatchMapping(path = "/categories")
    public CategoryDto updateCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Обработан PATCH-запрос /categories");
        return adminService.updateCategory(categoryDto);
    }

    @PostMapping(path = "/categories")
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Обработан POST-запрос /categories");
        return adminService.createCategory(newCategoryDto);
    }

    @DeleteMapping(path = "/categories/{catId}")
    public void deleteCategory(@PathVariable(name = "catId") int catId) {
        log.info("Обработан DELETE-запрос /categories/" + catId);
        adminService.deleteCategory(catId);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10") int size,
                                  @RequestParam(name = "ids", required = false) List<Integer> users) {
        log.info("Обработан GET-запрос /users");
        return adminService.getUsers(users, from, size);
    }

    @PostMapping("/users")
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Обработан POST-запрос /users");
        return adminService.createUser(newUserRequest);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable int userId) {
        log.info("Обработан DELETE-запрос /users/" + userId);
        adminService.deleteUser(userId);
    }

    @PostMapping("/compilations")
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Обработан POST-запрос /compilations");
        return adminService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable(name = "compId") int compId) {
        log.info("Обработан DELETE-запрос /compilations/" + compId);
        adminService.deleteCompilation(compId);
    }

    @DeleteMapping("/compilations/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable(name = "compId") int compId,
                                           @PathVariable(name = "eventId") int eventId) {
        log.info("Обработан DELETE-запрос /compilations/" + compId + "/events/" + eventId);
        adminService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/compilations/{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable(name = "compId") int compId,
                                      @PathVariable(name = "eventId") int eventId) {
        log.info("Обработан PATCH-запрос /compilations/" + compId + "/events/" + eventId);
        adminService.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/compilations/{compId}/pin")
    public void deletePinFromCompilation(@PathVariable(name = "compId") int compId) {
        log.info("Обработан DELETE-запрос /compilations/" + compId + "/pin");
        adminService.deletePinFromCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}/pin")
    public void addPinToCompilation(@PathVariable(name = "compId") int compId) {
        log.info("Обработан PATCH-запрос /compilations/" + compId + "/pin");
        adminService.addPinToCompilation(compId);
    }
}