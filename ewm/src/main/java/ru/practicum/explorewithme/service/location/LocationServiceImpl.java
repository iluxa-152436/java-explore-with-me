package ru.practicum.explorewithme.service.location;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.location.LocationDto;
import ru.practicum.explorewithme.dto.location.LocationRequest;
import ru.practicum.explorewithme.entity.Location;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.storage.LocationStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationStorage locationStorage;
    private final ModelMapper mapper;

    @Override
    public LocationDto addLocation(LocationRequest locationRequest) {
        return mapper.map(locationStorage.save(mapper.map(locationRequest, Location.class)), LocationDto.class);
    }

    @Override
    public LocationDto getLocationDto(long locationId) {
        return mapper.map(locationStorage.findById(locationId), LocationDto.class);
    }

    @Override
    public Location getLocation(long locationId) {
        return locationStorage.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id=" + locationId + " not found"));
    }

    @Override
    public List<LocationDto> getNearestLocation(long userId, double lon, double lat) {
        return locationStorage.findNearestByLonAndLat(lon, lat).stream().map(loc -> mapper.map(loc, LocationDto.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteLocation(long locationId) {
        locationStorage.deleteById(locationId);
    }
}
