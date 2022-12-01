package ru.practicum.ewm.entities;

import lombok.*;
import ru.practicum.ewm.util.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "EVENTS")
@NoArgsConstructor
@RequiredArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer id;
    @NonNull
    @Column(name = "annotation")
    private String annotation;
    @NonNull
    @Column(name = "category_id")
    private Integer categoryId;
    @NonNull
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @NonNull
    @Column(name = "description")
    private String description;
    @NonNull
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @NonNull
    @Column(name = "initiator_id")
    private Integer initiatorId;
    @NonNull
    @Column(name = "lat")
    private Float lat;
    @NonNull
    @Column(name = "lon")
    private Float lon;
    @NonNull
    @Column(name = "paid")
    private Boolean paid;
    @NonNull
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @NonNull
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @NonNull
    @Enumerated(EnumType.STRING)
    private EventState state;
    @NonNull
    @Column(name = "title")
    private String title;
}