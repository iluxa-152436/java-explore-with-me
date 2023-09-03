package ru.practicum.explorewithme.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventStorage extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndInitiatorId(long eventId, long initiatorId);

    Page<Event> findAllByInitiatorIdIn(List<Long> userIds, Pageable request);

    Page<Event> findAllByCategoryIdIn(List<Long> categoryIds, Pageable request);

    Page<Event> findAllByStateIn(List<EventState> states, Pageable request);

    Page<Event> findAllByEventDateBetween(LocalDateTime start, LocalDateTime end, Pageable request);

    Page<Event> findAllByInitiatorIdInAndStateIn(List<Long> userIds, List<EventState> states, Pageable request);

    Page<Event> findAllByInitiatorIdInAndCategoryIdIn(List<Long> userIds, List<Long> categoryIds, Pageable request);

    Page<Event> findAllByInitiatorIdInAndEventDateBetween(List<Long> userIds,
                                                          LocalDateTime start,
                                                          LocalDateTime end,
                                                          PageRequest request);

    Page<Event> findAllByStateInAndCategoryIdIn(List<EventState> states, List<Long> categoryIds, PageRequest request);

    Page<Event> findAllByStateInAndEventDateBetween(List<EventState> states,
                                                    LocalDateTime start,
                                                    LocalDateTime end,
                                                    PageRequest request);

    Page<Event> findAllByCategoryIdInAndEventDateBetween(List<Long> categoryIds,
                                                         LocalDateTime start,
                                                         LocalDateTime end,
                                                         PageRequest request);

    Page<Event> findAllByStateInAndCategoryIdInAndEventDateBetween(List<EventState> states,
                                                                   List<Long> categoryIds,
                                                                   LocalDateTime start,
                                                                   LocalDateTime end,
                                                                   PageRequest request);

    Page<Event> findAllByInitiatorIdInAndCategoryIdInAndEventDateBetween(List<Long> userIds,
                                                                         List<Long> categoryIds,
                                                                         LocalDateTime start,
                                                                         LocalDateTime end,
                                                                         PageRequest request);

    Page<Event> findAllByInitiatorIdInAndStateInAndCategoryIdIn(List<Long> userIds,
                                                                List<EventState> states,
                                                                List<Long> categoryIds,
                                                                PageRequest request);

    Page<Event> findAllByInitiatorIdInAndStateInAndEventDateBetween(List<Long> userIds,
                                                                    List<EventState> states,
                                                                    LocalDateTime start,
                                                                    LocalDateTime end,
                                                                    PageRequest request);

    Page<Event> findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(List<Long> userIds,
                                                                                   List<EventState> states,
                                                                                   List<Long> categoryIds,
                                                                                   LocalDateTime start,
                                                                                   LocalDateTime end,
                                                                                   PageRequest request);
}
