package ru.practicum.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.ParticipationRequest;
import ru.practicum.explorewithme.entity.ParticipationRequestState;

import java.util.List;
import java.util.Set;

public interface ParticipationRequestStorage extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequesterId(long userId);

    List<ParticipationRequest> findByEventId(long eventId);

    List<ParticipationRequest> findAllByIdIn(Set<Long> requestIds);

    long countByEventIdAndRequesterIdAndState(long eventId, long userId, ParticipationRequestState state);

    long countByEventIdAndState(long eventId, ParticipationRequestState state);
}
