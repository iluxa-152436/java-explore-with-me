package ru.practicum.explorewithme.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.entity.EventState;

import java.time.LocalDateTime;

import static ru.practicum.explorewithme.constant.DefaultValue.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    private long id;
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private LocationShortDto location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private EventState state;
    private String title;
    private long views;
}
