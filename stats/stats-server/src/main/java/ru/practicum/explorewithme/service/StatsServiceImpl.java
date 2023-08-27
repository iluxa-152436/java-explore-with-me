package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.entity.Stats;
import ru.practicum.explorewithme.storage.HitStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class StatsServiceImpl implements StatsService {
    private final HitStorage storage;

    @Override
    public List<Stats> findStats(LocalDateTime start, LocalDateTime end, boolean unique, Optional<List<String>> uris) {
        log.debug("find parameters: start {}, end {}, unique {}, uris {}", start, end, unique, uris);
        if (uris.isPresent()) {
            return unique ? storage.findUniqueStatByUris(start, end, uris.get()) : storage.findStatByUris(start, end, uris.get());
        } else {
            return unique ? storage.findUniqueStat(start, end) : storage.findStat(start, end);
        }
    }
}
