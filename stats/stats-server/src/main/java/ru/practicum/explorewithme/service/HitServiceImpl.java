package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.HitPostDto;
import ru.practicum.explorewithme.storage.HitStorage;
import ru.practicum.explorewithme.mapper.HitMapper;

@Slf4j
@RequiredArgsConstructor
@Service
public class HitServiceImpl implements HitService {
    private final HitStorage storage;

    @Override
    public void addNewHit(HitPostDto hitPostDto) {
        log.info("Add new hit: {}", hitPostDto);
        storage.save(HitMapper.toHitEntity(hitPostDto));
    }
}
