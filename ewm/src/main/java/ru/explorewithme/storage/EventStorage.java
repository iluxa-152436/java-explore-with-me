package ru.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.explorewithme.entity.Event;

public interface EventStorage extends JpaRepository<Event, Long> {
}
