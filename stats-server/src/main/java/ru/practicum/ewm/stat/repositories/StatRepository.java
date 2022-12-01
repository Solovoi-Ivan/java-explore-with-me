package ru.practicum.ewm.stat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.stat.entities.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Hit, Integer> {
    @Query("select new ru.practicum.ewm.stat.dto.ViewStats(h.app, h.uri, count(h.id))" +
            "from Hit as h " +
            "where h.hitTime between ?1 and ?2 " +
            "group by h.uri, h.app, h.ip " +
            "order by h.uri asc")
    List<ViewStats> getViews(LocalDateTime start, LocalDateTime end);

    @Query("select distinct h.app, h.uri, count (distinct h.ip) " +
            "from Hit as h " +
            "where h.hitTime between ?1 and ?2 " +
            "group by h.uri, h.app")
    List<List<Object>> getUniqueViews(LocalDateTime start, LocalDateTime end);
}