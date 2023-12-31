package ru.practicum.explorewithme.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationShortDto {
    private double lon;
    private double lat;
}
