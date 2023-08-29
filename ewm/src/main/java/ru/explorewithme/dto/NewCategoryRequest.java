package ru.explorewithme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewCategoryRequest {
    @NotBlank(message = "Field: name. Error: must not be blank.")
    private String name;
}
