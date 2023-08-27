package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.entity.Stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatsService {
    List<Stats> findStats(LocalDateTime start, LocalDateTime end, boolean unique, Optional<List<String>> uris);
}
