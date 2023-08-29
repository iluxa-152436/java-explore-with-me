package ru.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.explorewithme.entity.ParticipationRequest;

public interface ParticipationRequestStorage extends JpaRepository<ParticipationRequest, Long> {
}
