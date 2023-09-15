package ru.practicum.explorewithme.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationRequest {
    @NotNull
    @Max(90)
    @Min(-90)
    private double lat;
    @NotNull
    @Max(180)
    @Min(-180)
    private double lon;
    @NotBlank
    @Length(max = 100, min = 2)
    private String name;
    @NotBlank
    @Length(max = 100, min = 2)
    private String description;
}
