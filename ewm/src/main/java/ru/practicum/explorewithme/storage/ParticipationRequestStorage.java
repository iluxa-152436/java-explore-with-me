package ru.practicum.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.ParticipationRequest;

public interface ParticipationRequestStorage extends JpaRepository<ParticipationRequest, Long> {
}
