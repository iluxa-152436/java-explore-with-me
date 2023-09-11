package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.service.category.CategoryService;
import ru.practicum.explorewithme.service.compilation.CompilationService;
import ru.practicum.explorewithme.service.event.EventService;
import ru.practicum.explorewithme.service.event.TypeOfSorting;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.explorewithme.constant.DefaultValue.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PublicController {
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = FROM) int from,
                                           @RequestParam(defaultValue = SIZE) int size) {
        log.debug("Поступил запрос на получение категорий");
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable long categoryId) {
        log.debug("Поступил запрос на получение категории с id={}", categoryId);
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
        log.debug("Поступил запрос на получение событий с фильтрацией");
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
        log.debug("Поступил запрос на получение события с id={}", eventId);
        return eventService.getPublicEventById(eventId, request.getRemoteAddr());
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam Optional<Boolean> pinned,
                                                @RequestParam(defaultValue = FROM) int from,
                                                @RequestParam(defaultValue = SIZE) int size) {
        log.debug("Поступил запрос на получение подборок событий");
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable(name = "compId") long compilationId) {
        log.debug("Поступил запрос на получение подборки событий с id={}", compilationId);
        return compilationService.getCompilation(compilationId);
    }
}
