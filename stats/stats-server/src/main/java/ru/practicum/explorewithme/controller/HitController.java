package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.HitPostDto;
import ru.practicum.explorewithme.service.HitService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/hit")
public class HitController {
    private final HitService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void add(@RequestBody @Valid HitPostDto hitPostDto) {
        log.debug("New request with body: " + hitPostDto);
        service.addNewHit(hitPostDto);
    }
}
