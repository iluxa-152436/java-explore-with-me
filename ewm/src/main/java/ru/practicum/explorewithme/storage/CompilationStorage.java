package ru.practicum.explorewithme.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.entity.Compilation;

public interface CompilationStorage extends JpaRepository<Compilation, Long> {
    @Query("SELECT comp FROM Compilation as comp " +
            "WHERE (:pinned is null or comp.pinned = :pinned)")
    Page<Compilation> findByPinned(@Param("pinned") Boolean pinned, PageRequest pageable);
}
