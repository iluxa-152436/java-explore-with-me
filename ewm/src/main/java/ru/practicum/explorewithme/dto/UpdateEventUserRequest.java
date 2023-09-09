package ru.practicum.explorewithme.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.explorewithme.validator.EventDateConstraint;

import java.time.LocalDateTime;

import static ru.practicum.explorewithme.constant.DefaultValue.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {
    @Length(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Length(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @EventDateConstraint
    private LocalDateTime eventDate;
    private LocationShortDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    @Length(min = 3, max = 120)
    private String title;
    private StateActionUser stateAction;
}
