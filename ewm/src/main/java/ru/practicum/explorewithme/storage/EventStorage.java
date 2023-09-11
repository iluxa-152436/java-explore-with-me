package ru.practicum.explorewithme.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventStorage extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndInitiatorId(long eventId, long initiatorId);

    @Query("SELECT e FROM Event as e " +
            "WHERE e.state = COALESCE(:state, e.state) " +
            "AND e.eventDate >= COALESCE(:start, e.eventDate) " +
            "AND e.eventDate <= COALESCE(:end, e.eventDate) " +
            "AND (COALESCE (:cat, null) IS NULL OR e.category.id IN :cat) " +
            "AND e.paid = COALESCE(:paid, e.paid) " +
            "AND (LOWER(e.annotation) LIKE CASE WHEN :text IS NOT NULL THEN LOWER(CONCAT('%', :text, '%')) ELSE LOWER(e.annotation) END " +
            "OR LOWER(e.description) LIKE CASE WHEN :text IS NOT NULL THEN LOWER(CONCAT('%', :text, '%')) ELSE LOWER(e.description) END) " +
            "AND (:avail = false OR e.id IN " +
            "   (SELECT r.event.id " +
            "   FROM ParticipationRequest r " +
            "   WHERE r.state = 'CONFIRMED' " +
            "   GROUP BY r.event.id " +
            "   HAVING e.participantLimit - COUNT(id) > 0))")
    Page<Event> findAllForPublicWithFilters(@Param("end") LocalDateTime rangeEnd,
                                            @Param("start") LocalDateTime rangeStart,
                                            @Param("paid") Boolean paid,
                                            @Param("cat") List<Long> categories,
                                            @Param("avail") boolean onlyAvailable,
                                            @Param("text") String text,
                                            @Param("state") String state,
                                            PageRequest pageRequest);

    @Query("SELECT e FROM Event as e " +
            "WHERE (COALESCE (:state, null) IS NULL OR e.state IN :state) " +
            "AND e.eventDate >= COALESCE(:start, e.eventDate) " +
            "AND e.eventDate <= COALESCE(:end, e.eventDate) " +
            "AND (COALESCE (:cat, null) IS NULL OR e.category.id IN :cat) " +
            "AND (COALESCE (:user, null) IS NULL OR e.initiator.id IN :user)")
    Page<Event> findAllForAdminWithFilters(@Param("end") LocalDateTime rangeEnd,
                                           @Param("start") LocalDateTime rangeStart,
                                           @Param("user") List<Long> users,
                                           @Param("cat") List<Long> categories,
                                           @Param("state") List<EventState> states,
                                           PageRequest pageRequest);

    Optional<Event> findByIdAndState(long eventId, EventState state);

    boolean existsByIdAndInitiatorId(long eventId, long userId);

    List<Event> findAllByIdIn(List<Long> events);
}