package ru.practicum.explorewithme.dto.participationRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.explorewithme.constant.DefaultValue.DATE_TIME_PATTERN;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

public class ParticipationRequestDto {
    private long id;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;
    private long event;
    private long requester;
    private String status;
}
