package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.CompilationDto;
import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.service.CategoryService;
import ru.practicum.explorewithme.service.CompilationService;
import ru.practicum.explorewithme.service.EventService;
import ru.practicum.explorewithme.service.TypeOfSorting;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.explorewithme.constant.DefaultValue.*;

@RequiredArgsConstructor
@RestController
public class PublicController {
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

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
                                         @RequestParam(defaultValue = "EVENT_DATE") TypeOfSorting sort,
                                         HttpServletRequest request) {
        return eventService.getEvents(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                from,
                size,
                sort,
                request.getRemoteAddr());
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEvent(@PathVariable long eventId, HttpServletRequest request) {
        return eventService.getPublicEventById(eventId, request.getRemoteAddr());
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam Optional<Boolean> pinned,
                                                @RequestParam(defaultValue = FROM) int from,
                                                @RequestParam(defaultValue = SIZE) int size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable(name = "compId") long compilationId) {
        return compilationService.getCompilation(compilationId);
    }
}
