package ru.practicum.ewm.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Location {
    @NonNull
    private Float lat;
    @NonNull
    private Float lon;
}