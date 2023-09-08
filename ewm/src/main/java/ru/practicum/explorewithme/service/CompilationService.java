package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.CompilationDto;
import ru.practicum.explorewithme.dto.NewCompilationDto;
import ru.practicum.explorewithme.dto.UpdateCompilationDto;

import java.util.List;
import java.util.Optional;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(long compilationId);

    CompilationDto updateCompilation(long compilationId, UpdateCompilationDto updateCompilationDto);

    List<CompilationDto> getCompilations(Optional<Boolean> pinned, int from, int size);

    CompilationDto getCompilation(long compilationId);
}
