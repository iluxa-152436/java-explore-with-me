package ru.practicum.explorewithme.dto.location;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@AllArgsConstructor
@Data
public class LocationUpdateRequest {
    @Max(90)
    @Min(-90)
    private Double lat;
    @Max(90)
    @Min(-90)
    private Double lon;
    @Length(max = 100, min = 2)
    private String name;
    @Length(max = 100, min = 2)
    private String description;
}
