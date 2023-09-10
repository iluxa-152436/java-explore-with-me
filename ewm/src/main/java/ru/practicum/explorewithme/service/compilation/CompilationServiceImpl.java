package ru.practicum.explorewithme.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationDto;
import ru.practicum.explorewithme.entity.Compilation;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.CompilationMapper;
import ru.practicum.explorewithme.service.event.EventService;
import ru.practicum.explorewithme.entity.RequestPage;
import ru.practicum.explorewithme.service.request.ParticipationRequestService;
import ru.practicum.explorewithme.storage.CompilationStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationMapper compilationMapper;
    private final EventService eventService;
    private final CompilationStorage storage;
    private final ParticipationRequestService requestService;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        log.debug("Список id событий для подборки {}", newCompilationDto.getEvents());
        List<Event> events = eventService.getEvents(List.copyOf(newCompilationDto.getEvents()));
        log.debug("Список событий для подборки {}", events);
        return compilationMapper.toCompilationDto(storage.save(compilationMapper.toEntity(newCompilationDto, events)),
                getMapEventConfirmed(events));
    }

    @Override
    public void deleteCompilation(long compilationId) {
        checkCompilation(compilationId);
        storage.deleteById(compilationId);
    }

    @Override
    public CompilationDto updateCompilation(long compilationId, UpdateCompilationDto updateCompilationDto) {
        log.debug("Новый список id событий для подборки {}", updateCompilationDto.getEvents());
        Compilation compilation = getCompilationWithCheck(compilationId);
        log.debug("Старый список событий для подборки {}", compilation.getEvents());
        if (Optional.ofNullable(updateCompilationDto.getEvents()).isPresent()) {
            List<Event> events = eventService.getEvents(List.copyOf(updateCompilationDto.getEvents()));
            return compilationMapper.toCompilationDto(storage.save(compilationMapper.toEntity(compilation,
                    updateCompilationDto,
                    Optional.of(events))), getMapEventConfirmed(events));
        } else {
            return compilationMapper.toCompilationDto(storage.save(compilationMapper.toEntity(compilation,
                    updateCompilationDto,
                    Optional.empty())), Collections.EMPTY_MAP);
        }
    }

    private Map<Long, Long> getMapEventConfirmed(List<Event> events) {
        return events.stream().collect(Collectors.toMap(Event::getId,
                event -> requestService.getNumberOfConfirmed(event.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Optional<Boolean> pinned, int from, int size) {
        return storage.findByPinned(pinned.orElse(null),
                        RequestPage.getPageable(from, size, Optional.empty())).stream()
                .map((c) -> compilationMapper.toCompilationDto(c, getMapEventConfirmed(List.copyOf(c.getEvents()))))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(long compilationId) {
        Compilation compilation = getCompilationWithCheck(compilationId);
        return compilationMapper.toCompilationDto(compilation, getMapEventConfirmed(List.copyOf(compilation.getEvents())));
    }

    @Transactional(readOnly = true)
    private Compilation getCompilationWithCheck(long compilationId) {
        return storage.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compilationId + " not found"));
    }

    @Transactional(readOnly = true)
    private void checkCompilation(long compilationId) {
        if (!storage.existsById(compilationId)) {
            throw new NotFoundException("Compilation with id=" + compilationId + " not found");
        }
    }
}