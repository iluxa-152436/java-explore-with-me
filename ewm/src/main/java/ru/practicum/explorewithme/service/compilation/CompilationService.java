package ru.practicum.explorewithme.service.compilation;

import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationDto;

import java.util.List;
import java.util.Optional;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(long compilationId);

    CompilationDto updateCompilation(long compilationId, UpdateCompilationDto updateCompilationDto);

    List<CompilationDto> getCompilations(Optional<Boolean> pinned, int from, int size);

    CompilationDto getCompilation(long compilationId);
}
