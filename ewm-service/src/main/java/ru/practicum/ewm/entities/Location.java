package ru.practicum.ewm.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Location {
    @NonNull
    private Float lat;
    @NonNull
    private Float lon;
}