package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.explorewithme.dto.location.LocationShortDto;
import ru.practicum.explorewithme.validator.EventDateConstraint;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NewEventRequest {
    @NotNull
    @Length(min = 20, max = 2000)
    private String annotation;
    @Positive
    private long category;
    @NotNull
    @Length(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @EventDateConstraint
    private LocalDateTime eventDate;
    @NotNull
    private LocationShortDto location;
    private boolean paid;
    @PositiveOrZero
    private int participantLimit;
    private Boolean requestModeration;
    @NotNull
    @Length(min = 3, max = 120)
    private String title;
}
