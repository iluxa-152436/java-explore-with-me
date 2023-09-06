package ru.practicum.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.ParticipationRequest;
import ru.practicum.explorewithme.entity.ParticipationRequestState;

import java.util.List;

public interface ParticipationRequestStorage extends JpaRepository<ParticipationRequest, Long> {
    Long countByRequesterIdAndEventIdAndState(long userId, long eventId, ParticipationRequestState state);

    List<ParticipationRequest> findByRequesterId(long userId);
}
