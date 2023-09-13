package ru.practicum.explorewithme.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.entity.Event;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationDto {
    private long id;
    private double lat;
    private double lon;
    private Set<Event> events = new HashSet<>();
    private String name;
    private String description;
}
