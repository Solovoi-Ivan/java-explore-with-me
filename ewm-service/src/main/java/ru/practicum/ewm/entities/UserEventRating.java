package ru.practicum.ewm.entities;

import lombok.*;
import ru.practicum.ewm.util.UserEventRatingId;

import javax.persistence.*;

@Getter
@Setter
@Entity
@IdClass(UserEventRatingId.class)
@Table(name = "USER_EVENT_RATINGS")
@NoArgsConstructor
@AllArgsConstructor
public class UserEventRating {
    @Id
    @Column(name = "user_id")
    private Integer userId;
    @Id
    @Column(name = "event_id")
    private Integer eventId;
    @NonNull
    @Column(name = "is_positive")
    private Boolean isPositive;
}