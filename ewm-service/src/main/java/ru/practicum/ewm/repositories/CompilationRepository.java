package ru.practicum.ewm.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entities.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
    List<Compilation> findByPinned(boolean pinned, PageRequest pageRequest);
}
