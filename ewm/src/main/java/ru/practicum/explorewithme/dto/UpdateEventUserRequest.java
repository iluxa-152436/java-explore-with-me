package ru.practicum.explorewithme.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.entity.StateAction;
import ru.practicum.explorewithme.validator.EventDateConstraint;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {
    private String annotation;
    private Long category;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @EventDateConstraint
    private LocalDateTime eventDate;
    private LocationShortDto location;
    private Boolean paid;
    private int participantLimit;
    private Boolean requestModeration;
    private String title;
    private StateAction stateAction;
}
