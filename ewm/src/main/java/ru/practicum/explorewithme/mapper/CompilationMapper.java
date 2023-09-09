package ru.practicum.explorewithme.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.dto.CompilationDto;
import ru.practicum.explorewithme.dto.NewCompilationDto;
import ru.practicum.explorewithme.dto.UpdateCompilationDto;
import ru.practicum.explorewithme.entity.Compilation;
import ru.practicum.explorewithme.entity.Event;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public CompilationDto toCompilationDto(Compilation compilation, Map<Long, Long> mapNumberOfConfirmed) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream()
                        .map((event) -> eventMapper.toEventShortDto(event, mapNumberOfConfirmed.get(event.getId())))
                        .collect(Collectors.toList()))
                .pinned(compilation.isPinned())
                .build();
    }

    public Compilation toEntity(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .events(Set.copyOf(events))
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .build();
    }

    public Compilation toEntity(Compilation compilation,
                                UpdateCompilationDto updateCompilationDto,
                                Optional<List<Event>> newEvents) {
        if (newEvents.isPresent()) {
            compilation.setEvents(new HashSet<>(newEvents.get()));
        }
        Optional.ofNullable(updateCompilationDto.getPinned()).ifPresent(compilation::setPinned);
        Optional.ofNullable(updateCompilationDto.getTitle()).ifPresent(compilation::setTitle);
        return compilation;
    }
}
