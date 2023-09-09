package ru.practicum.explorewithme.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewCategoryRequest {
    @NotBlank(message = "Field: name. Error: must not be blank.")
    @Length(min = 1, max = 50)
    private String name;
}
