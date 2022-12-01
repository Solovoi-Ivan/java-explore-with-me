package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
