package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.service.CategoryService;
import ru.practicum.explorewithme.service.EventService;
import ru.practicum.explorewithme.service.TypeOfSorting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.explorewithme.constant.DefaultValue.*;

@RequiredArgsConstructor
@RestController
public class PublicController {
    private final CategoryService categoryService;
    private final EventService eventService;

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = FROM) int from,
                                           @RequestParam(defaultValue = SIZE) int size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEvents(@RequestParam Optional<String> text,
                                        @RequestParam Optional<List<Long>> categories,
                                        @RequestParam Optional<Boolean> paid,
                                        @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) Optional<LocalDateTime> rangeStart,
                                        @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) Optional<LocalDateTime> rangeEnd,
                                        @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                        @RequestParam(defaultValue = FROM) int from,
                                        @RequestParam(defaultValue = SIZE) int size,
                                        @RequestParam(defaultValue = "EVENT_DATE") TypeOfSorting sort) {
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size, sort);
    }
}
