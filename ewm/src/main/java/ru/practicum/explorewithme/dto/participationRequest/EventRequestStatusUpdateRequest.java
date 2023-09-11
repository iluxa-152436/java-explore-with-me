package ru.practicum.explorewithme.dto.participationRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.explorewithme.entity.ParticipationRequestState;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotEmpty
    private Set<Long> requestIds;
    @NotNull
    private ParticipationRequestState status;
}
