package ru.practicum.ewm.entities;

import lombok.*;
import ru.practicum.ewm.util.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "PARTICIPATION_REQUESTS")
@NoArgsConstructor
@RequiredArgsConstructor
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer id;
    @NonNull
    @Column(name = "created")
    private LocalDateTime created;
    @NonNull
    @Column(name = "event_id")
    private Integer eventId;
    @NonNull
    @Column(name = "requester_id")
    private Integer requesterId;
    @NonNull
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
