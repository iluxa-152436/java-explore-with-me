package ru.practicum.explorewithme.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.validator.EventDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NewEventRequest {
    @NotBlank
    private String annotation;
    @Positive
    private long category;
    @NotBlank
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @EventDateConstraint
    private LocalDateTime eventDate;
    private LocationShortDto location;
    private boolean paid;
    private int participantLimit;
    private Boolean requestModeration;
    @NotBlank
    private String title;
}
