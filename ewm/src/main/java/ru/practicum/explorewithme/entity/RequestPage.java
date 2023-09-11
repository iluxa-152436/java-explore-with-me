package ru.practicum.explorewithme.entity;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.explorewithme.service.event.TypeOfSorting;

import java.util.Optional;

public class RequestPage {
    public static PageRequest getPageable(int from, int size, Optional<TypeOfSorting> sort) {
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("From and size must be valid");
        }
        if (sort.isEmpty()) {
            return PageRequest.of(from / size, size);
        } else {
            switch (sort.get()) {
                case VIEWS:
                    return PageRequest.of(from / size, size);
                case EVENT_DATE:
                    return PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
}
