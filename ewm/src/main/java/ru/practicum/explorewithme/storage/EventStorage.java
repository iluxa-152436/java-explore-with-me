package ru.practicum.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.Event;

public interface EventStorage extends JpaRepository<Event, Long> {
}
