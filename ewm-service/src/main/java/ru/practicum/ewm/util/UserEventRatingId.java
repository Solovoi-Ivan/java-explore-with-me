package ru.practicum.ewm.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEventRatingId implements Serializable {
    private Integer userId;
    private Integer eventId;
}