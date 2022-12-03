package ru.practicum.ewm.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.util.EventState;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByCategoryId(int categoryId);

    List<Event> findByInitiatorId(int initiatorId, PageRequest pageRequest);

    List<Event> findByInitiatorIdAndState(int initiatorId, EventState eventState);

    List<Event> findByState(EventState state);

    @Query("select e from Event e where (lower(e.annotation) like lower(concat('%', ?1, '%'))" +
            "or lower(e.description) like lower(concat('%', ?1, '%'))) and e.state = 'PUBLISHED'")
    List<Event> search(String text);
}
