package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.entity.EventState;
import ru.practicum.explorewithme.service.CategoryService;
import ru.practicum.explorewithme.service.CompilationService;
import ru.practicum.explorewithme.service.EventService;
import ru.practicum.explorewithme.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.explorewithme.constant.DefaultValue.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.debug("Поступил запрос от администратора на создание пользователя");
        return userService.addNewUser(newUserRequest);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam Optional<List<Long>> ids,
                                  @RequestParam(defaultValue = FROM) int from,
                                  @RequestParam(defaultValue = SIZE) int size) {
        log.debug("Поступил запрос от администратора на получение пользователей с id={}", ids);
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id) {
        log.debug("Поступил запрос от администратора на получение пользователя с id={}", id);
        userService.deleteUser(id);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@RequestBody @Valid NewCategoryRequest newCategoryRequest) {
        log.debug("Поступил запрос от администратора на создание категории");
        return categoryService.addNewCategory(newCategoryRequest);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long id) {
        log.debug("Поступил запрос от администратора на создание удаление категории с id={}", id);
        categoryService.deleteCategory(id);
    }

    @PatchMapping("/categories/{id}")
    public CategoryDto patchCategory(@RequestBody @Valid NewCategoryRequest newCategoryRequest,
                                     @PathVariable long id) {
        log.debug("Поступил запрос от администратора на обновление пользователя с id={}", id);
        return categoryService.updateCategory(id, newCategoryRequest);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam Optional<List<Long>> users,
                                        @RequestParam Optional<List<EventState>> states,
                                        @RequestParam Optional<List<Long>> categories,
                                        @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) Optional<LocalDateTime> rangeStart,
                                        @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) Optional<LocalDateTime> rangeEnd,
                                        @RequestParam(defaultValue = FROM) int from,
                                        @RequestParam(defaultValue = SIZE) int size) {
        log.debug("Поступил запрос от администратора на получение событий с фильтрацией");
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto patchEvent(@PathVariable long eventId,
                                   @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.debug("Поступил запрос от администратора на изменение события с id={}", eventId);
        return eventService.updateEvent(eventId, updateEventAdminRequest);

    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.debug("Поступил запрос от администратора на создание подборки событий");
        return compilationService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") long compilationId) {
        log.debug("Поступил запрос от администратора на удаление подборки событий с id={}", compilationId);
        compilationService.deleteCompilation(compilationId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto patchCompilation(@PathVariable(name = "compId") long compilationId,
                                           @RequestBody UpdateCompilationDto updateCompilationDto) {
        log.debug("Поступил запрос от администратора на изменение подборки событий с id={}", compilationId);
        return compilationService.updateCompilation(compilationId, updateCompilationDto);
    }

}
