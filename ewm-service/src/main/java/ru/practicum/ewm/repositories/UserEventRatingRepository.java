package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entities.UserEventRating;
import ru.practicum.ewm.util.UserEventRatingId;

import java.util.List;

public interface UserEventRatingRepository extends JpaRepository<UserEventRating, UserEventRatingId> {
    List<UserEventRating> findByEventId(int eventId);
}
