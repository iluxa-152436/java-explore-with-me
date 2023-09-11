package ru.practicum.explorewithme.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Setter
@Getter
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "description", nullable = false, length = 7000)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @ToString.Exclude
    private Location location;
    @Column(name = "paid")
    private boolean paid;
    @Column(name = "participant_limit")
    private int participantLimit;
    @Column(name = "request_moderation", nullable = false)
    private boolean requestModeration;
    @Column(name = "title", nullable = false, length = 120)
    private String title;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Column(name = "published")
    private LocalDateTime published;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventState state;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    @ToString.Exclude
    private User initiator;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id && paid == event.paid
                && participantLimit == event.participantLimit
                && requestModeration == event.requestModeration
                && annotation.equals(event.annotation)
                && category.equals(event.category)
                && description.equals(event.description)
                && eventDate.equals(event.eventDate)
                && location.equals(event.location)
                && title.equals(event.title)
                && created.equals(event.created)
                && Objects.equals(published, event.published)
                && state == event.state
                && initiator.equals(event.initiator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                annotation,
                category,
                description,
                eventDate,
                location,
                paid,
                participantLimit,
                requestModeration,
                title,
                created,
                published,
                state,
                initiator);
    }
}
