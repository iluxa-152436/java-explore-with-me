package ru.practicum.explorewithme.service.location;

import ru.practicum.explorewithme.dto.location.LocationDto;
import ru.practicum.explorewithme.dto.location.LocationRequest;
import ru.practicum.explorewithme.dto.location.LocationUpdateRequest;
import ru.practicum.explorewithme.entity.Location;

import java.util.List;
import java.util.Optional;

public interface LocationService {
    LocationDto addLocation(LocationRequest locationRequest);

    LocationDto getLocationDto(long locationId);

    Location getLocation(long locationId);

    List<LocationDto> getNearestApprovedLocation(long userId, double lon, double lat);

    void deleteLocation(long locationId);

    LocationDto updateLocation(long locationId, LocationUpdateRequest updateRequest);

    Optional<Location> getAdmLocationByGeoAndApproved(double lon, double lat, boolean approved);
}
