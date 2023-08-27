package ru.practicum.explorewithme.mapper;

import ru.practicum.explorewithme.entity.Hit;

public class HitMapper {
    public static Hit toHitEntity(HitPostDto hitPostDto) {
        return Hit.builder()
                .app(hitPostDto.getApp())
                .ip(hitPostDto.getIp())
                .uri(hitPostDto.getUri())
                .timeStamp(hitPostDto.getTimestamp())
                .build();
    }
}
