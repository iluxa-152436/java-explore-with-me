package ru.practicum.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.entity.Location;

import java.util.List;

public interface LocationStorage extends JpaRepository<Location, Long> {
    @Query("SELECT loc FROM Location AS loc " +
            "ORDER BY distance(:lat, :lon, loc.lat, loc.lon) ")
    List<Location> findNearestByLonAndLat(@Param("lon") double lon, @Param("lat")  double lat);
}
