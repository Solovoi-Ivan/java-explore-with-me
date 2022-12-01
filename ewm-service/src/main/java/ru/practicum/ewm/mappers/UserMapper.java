package ru.practicum.ewm.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.entities.User;

@Component
public class UserMapper {
    public UserDto toDto(User u) {
        return new UserDto(u.getEmail(), u.getId(), u.getName());
    }

    public UserShortDto toShortDto(User u) {
        return new UserShortDto(u.getId(), u.getName());
    }

    public User fromDto(NewUserRequest u) {
        return new User(u.getName(), u.getEmail());
    }
}