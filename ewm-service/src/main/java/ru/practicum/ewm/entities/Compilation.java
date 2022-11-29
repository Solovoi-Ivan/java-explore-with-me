package ru.practicum.ewm.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "COMPILATIONS")
@NoArgsConstructor
@RequiredArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Integer id;
    @NonNull
    @Column(name = "pinned")
    private Boolean pinned;
    @NonNull
    @Column(name = "title")
    private String title;
    @NonNull
    @ManyToMany
    @JoinTable(
            name = "EVENTS_COMPILATIONS",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events;
}
