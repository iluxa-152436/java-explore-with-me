package ru.practicum.explorewithme.entity;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Table(name = "locations")
@EqualsAndHashCode(exclude = "event")
@ToString(exclude = "event")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "lat", nullable = false)
    private double lat;
    @Column(name = "lon", nullable = false)
    private double lon;
    @OneToOne(mappedBy = "location")
    private Event event;
}
