package ru.practicum.explorewithme.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private ParticipationRequestState state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipationRequest request = (ParticipationRequest) o;
        return id == request.id && created.equals(request.created)
                && event.equals(request.event)
                && requester.equals(request.requester)
                && state == request.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, created, event, requester, state);
    }
}
