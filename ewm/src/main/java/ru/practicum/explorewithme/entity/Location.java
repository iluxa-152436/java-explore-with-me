package ru.practicum.explorewithme.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Table(name = "locations")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return id == location.id && Double.compare(location.lat, lat) == 0
                && Double.compare(location.lon, lon) == 0
                && Objects.equals(event, location.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lat, lon);
    }
}
