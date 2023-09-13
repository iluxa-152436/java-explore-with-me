package ru.practicum.explorewithme.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationShortDto {
    private Long id;
    private double lon;
    private double lat;
}
