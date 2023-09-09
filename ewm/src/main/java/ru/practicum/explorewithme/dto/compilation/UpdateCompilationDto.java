package ru.practicum.explorewithme.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateCompilationDto {
    private Set<Long> events;
    private Boolean pinned;
    @Length(min = 1, max = 50)
    private String title;
}
