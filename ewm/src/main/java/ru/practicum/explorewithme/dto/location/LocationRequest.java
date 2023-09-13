package ru.practicum.explorewithme.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationRequest {
    private double lat;
    private double lon;
    @NotBlank
    @Length(max = 100, min = 2)
    private String name;
    @NotBlank
    @Length(max = 100, min = 2)
    private String description;
}
