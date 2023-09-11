package ru.practicum.explorewithme.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NewCompilationDto {
    private Set<Long> events = new HashSet<>();
    private boolean pinned = false;
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
}
