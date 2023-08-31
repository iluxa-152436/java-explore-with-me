package ru.practicum.explorewithme.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "description", nullable = false, length = 7000)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
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
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
}
