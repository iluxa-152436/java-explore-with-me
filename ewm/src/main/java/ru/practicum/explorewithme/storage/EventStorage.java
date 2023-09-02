package ru.practicum.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.Event;

import java.util.Optional;

public interface EventStorage extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndInitiatorId(long eventId, long initiatorId);
}
