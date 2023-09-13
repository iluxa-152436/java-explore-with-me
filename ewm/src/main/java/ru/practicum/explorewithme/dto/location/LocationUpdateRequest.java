package ru.practicum.explorewithme.dto.location;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Value
@AllArgsConstructor
@Data
@Getter
public class LocationUpdateRequest {
    @Max(90)
    @Min(-90)
    Double lat;
    @Max(90)
    @Min(-90)
    Double lon;
    @Length(max = 100, min = 2)
    String name;
    @Length(max = 100, min = 2)
    String description;
}
