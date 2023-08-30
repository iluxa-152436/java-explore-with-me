package ru.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.explorewithme.dto.EventFullDto;
import ru.explorewithme.dto.NewEventRequest;
import ru.explorewithme.service.EventService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    public EventFullDto postEvent(@RequestBody @Valid NewEventRequest newEventRequest,
                                    @PathVariable long userId) {
        return eventService.addNewEvent(userId, newEventRequest);
    }
}
