package ru.practicum.ewm.stat.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "HIT_STATS")
@NoArgsConstructor
@RequiredArgsConstructor
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hit_id")
    private Integer id;
    @NonNull
    @Column(name = "app")
    private String app;
    @NonNull
    @Column(name = "uri")
    private String uri;
    @NonNull
    @Column(name = "ip")
    private String ip;
    @NonNull
    @Column(name = "hit_time")
    private LocalDateTime hitTime;
}
