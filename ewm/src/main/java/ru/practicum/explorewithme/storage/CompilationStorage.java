package ru.practicum.explorewithme.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.Compilation;

public interface CompilationStorage extends JpaRepository<Compilation, Long> {
    Page<Compilation> findByPinned(boolean pinned, PageRequest pageable);
}
