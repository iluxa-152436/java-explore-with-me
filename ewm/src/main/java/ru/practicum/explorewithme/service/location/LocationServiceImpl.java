package ru.practicum.explorewithme.service.location;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.location.LocationDto;
import ru.practicum.explorewithme.dto.location.LocationRequest;
import ru.practicum.explorewithme.dto.location.LocationUpdateRequest;
import ru.practicum.explorewithme.entity.Location;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.storage.LocationStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {
    private final LocationStorage locationStorage;
    private final ModelMapper mapper;

    @Override
    public LocationDto addLocation(LocationRequest locationRequest) {
        Location location = mapper.map(locationRequest, Location.class);
        if (locationStorage.findByLonAndLatAndApproved(location.getLon(), location.getLat(), true).isPresent()) {
            throw new IllegalArgumentException("Уже существует");
        }
        location.setApproved(true);
        return mapper.map(locationStorage.save(location), LocationDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationDto getLocationDto(long locationId) {
        return mapper.map(locationStorage.findById(locationId), LocationDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Location getLocation(long locationId) {
        return locationStorage.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id=" + locationId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDto> getNearestApprovedLocation(long userId, double lon, double lat) {
        return locationStorage.findNearestByLonAndLat(lon, lat, true).stream()
                .map(loc -> mapper.map(loc, LocationDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLocation(long locationId) {
        locationStorage.deleteById(locationId);
    }

    @Override
    public LocationDto updateLocation(long locationId, LocationUpdateRequest updateRequest) {
        Location location = getLocation(locationId);
        Optional.ofNullable(updateRequest.getLon()).ifPresent(location::setLon);
        Optional.ofNullable(updateRequest.getLat()).ifPresent(location::setLat);
        Optional.ofNullable(updateRequest.getDescription()).ifPresent(location::setDescription);
        Optional.ofNullable(updateRequest.getName()).ifPresent(location::setName);
        return mapper.map(location, LocationDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Location> getAdmLocationByGeoAndApproved(double lon, double lat, boolean approved) {
        return locationStorage.findByLonAndLatAndApproved(lon, lat, approved);
    }
}
