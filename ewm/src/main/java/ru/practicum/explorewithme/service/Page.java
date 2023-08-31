package ru.practicum.explorewithme.service;

import org.springframework.data.domain.PageRequest;

public class Page {
    public static PageRequest getPageable(int from, int size) {
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("From and size must be valid");
        }
        return PageRequest.of(from / size, size);
    }
}
