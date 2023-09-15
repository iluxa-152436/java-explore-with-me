package ru.practicum.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.entity.Location;

import java.util.List;
import java.util.Optional;

public interface LocationStorage extends JpaRepository<Location, Long> {
    @Query("SELECT loc FROM Location AS loc " +
            "WHERE loc.approved = :approved " +
            "ORDER BY distance(:lat, :lon, loc.lat, loc.lon) ")
    List<Location> findNearestByLonAndLat(@Param("lon") double lon,
                                          @Param("lat") double lat,
                                          @Param("approved") boolean approved);

    Optional<Location> findByLonAndLatAndApproved(double lon, double lat, boolean approved);
}
