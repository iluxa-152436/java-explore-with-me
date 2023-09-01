package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.entity.Stats;
import ru.practicum.explorewithme.service.StatsService;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/stats")
public class StatsController {
    private final StatsService service;

    @GetMapping
    public List<Stats> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                @RequestParam(defaultValue = "false") boolean unique,
                                @RequestParam(required = false) Optional<List<String>> uris,
                                HttpServletResponse response) {
        log.debug("get parameters: start {}, end {}, unique {}, uris {}", start, end, unique, uris);
        List<Stats> result = service.findStats(start, end, unique, uris);
        if (result == null || result.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        return result;
    }
}
